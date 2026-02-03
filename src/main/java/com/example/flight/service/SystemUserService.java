package com.example.flight.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.controller.dto.RegisterRequest;
import com.example.flight.entity.Passenger;
import com.example.flight.entity.SystemUser;
import com.example.flight.exception.ResourceNotFoundException;
import com.example.flight.exception.ValidationException;
import com.example.flight.repository.SystemUserRepository;

@Service
public class SystemUserService implements UserDetailsService {

    private final SystemUserRepository systemUserRepository;
    private final PassengerService passengerService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SystemUserService(SystemUserRepository systemUserRepository, 
                             PassengerService passengerService,
                             @Lazy PasswordEncoder passwordEncoder) {
        this.systemUserRepository = systemUserRepository;
        this.passengerService = passengerService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = systemUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return new User(
            systemUser.getUsername(),
            systemUser.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + systemUser.getRole().name()))
        );
    }
    
    public SystemUser saveSystemUserWithoutReEncoding(SystemUser user) {
        return systemUserRepository.save(user);
    }

    public SystemUser saveSystemUser(SystemUser systemUser) {
        // 如果密码不为空，且不是加密后的格式（以$2a$开头），则进行加密
        if (systemUser.getPassword() != null && !systemUser.getPassword().isEmpty() && !systemUser.getPassword().startsWith("$2a$")) {
            systemUser.setPassword(passwordEncoder.encode(systemUser.getPassword()));
        }
        if (systemUser.getCreatedAt() == null) {
            systemUser.setCreatedAt(LocalDateTime.now());
        }
        return systemUserRepository.save(systemUser);
    }
    
    @Transactional
    public void updateLastLoginTransactional(Integer userId) {
        systemUserRepository.findById(userId).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            systemUserRepository.save(user);
        });
    }

    public List<SystemUser> findAll() { return systemUserRepository.findAll(); }
    public Optional<SystemUser> findById(Integer id) { return systemUserRepository.findById(id); }
    public Optional<SystemUser> findByUsername(String username) { return systemUserRepository.findByUsernameWithPassenger(username); }
    public boolean existsByUsername(String username) { return systemUserRepository.existsByUsername(username); }
    public List<SystemUser> findByRole(SystemUser.UserRole role) { return systemUserRepository.findByRole(role); }
    public void deleteSystemUser(Integer id) { systemUserRepository.deleteById(id); }
    public boolean existsByPassenger(Passenger passenger) { return systemUserRepository.existsByPassenger(passenger); }

    // --- 核心注册逻辑 ---
    @Transactional
    public SystemUser registerNewUser(RegisterRequest req) {
        // 调试日志：检查接收到的参数
        System.out.println("【注册调试】正在注册用户: " + req.getUsername());
        System.out.println("【注册调试】接收到的密码长度: " + (req.getPassword() == null ? "NULL" : req.getPassword().length()));

        // 1. 检查用户名
        if (existsByUsername(req.getUsername())) {
            throw new ValidationException("用户名 '" + req.getUsername() + "' 已被占用");
        }

        // 2. 获取或创建乘客信息
        Passenger passenger = passengerService.createOrGetPassenger(
            req.getName(), 
            req.getIdCard(), 
            req.getPhone(), 
            req.getEmail()
        );

        // 3. 检查该乘客是否已经绑定了系统账号
        if (existsByPassenger(passenger)) {
            throw new ValidationException("该身份证号已绑定其他账号，请直接登录");
        }

        // 4. 创建系统用户
        SystemUser newUser = new SystemUser();
        newUser.setUsername(req.getUsername());
        
        // ★★★ 强制检查密码 ★★★
        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new ValidationException("注册失败：密码不能为空");
        }
        // 设置密码（这行代码非常关键，之前可能漏了或没生效）
        newUser.setPassword(req.getPassword()); 
        
        newUser.setFullName(passenger.getName());
        newUser.setRole(SystemUser.UserRole.PASSENGER);
        newUser.setPassenger(passenger);
        
        // 5. 保存并返回 (saveSystemUser方法负责加密密码)
        SystemUser savedUser = saveSystemUser(newUser);
        return savedUser;
    }

    // --- 常用乘机人管理逻辑 ---

    @Transactional
    public List<Passenger> getMyContacts(Integer userId) {
        SystemUser user = systemUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Passenger> contacts = user.getContacts();
        contacts.size(); // 触发懒加载
        return contacts;
    }

    @Transactional
    public void addContact(Integer userId, RegisterRequest contactInfo) {
        SystemUser user = systemUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Passenger contact = passengerService.createOrGetPassenger(
            contactInfo.getName(),
            contactInfo.getIdCard(),
            contactInfo.getPhone(),
            contactInfo.getEmail()
        );

        if (user.getContacts().contains(contact)) {
            throw new ValidationException("该乘客已在您的常用列表中");
        }

        user.getContacts().add(contact);
        systemUserRepository.save(user);
    }

    @Transactional
    public void removeContact(Integer userId, Integer passengerId) {
        SystemUser user = systemUserRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Passenger passengerToRemove = passengerService.findById(passengerId)
            .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        if (user.getContacts().remove(passengerToRemove)) {
            systemUserRepository.save(user);
        } else {
            throw new ValidationException("该乘客不在您的常用列表中");
        }
    }
    
    // --- 密码重置相关 ---
    @Transactional(readOnly = true)
    public void validateResetRequest(String username, String idCard, String phone) {
        SystemUser user = systemUserRepository.findByUsernameWithPassenger(username)
            .orElseThrow(() -> new ValidationException("用户名不存在"));
        if (user.getRole() != SystemUser.UserRole.PASSENGER) throw new ValidationException("仅支持乘客用户");
        Passenger p = user.getPassenger();
        if (p == null) throw new ValidationException("未关联乘客信息");
        if (!p.getIdCard().equals(idCard)) throw new ValidationException("身份证号不匹配");
        if (!p.getPhone().equals(phone)) throw new ValidationException("手机号不匹配");
    }

    @Transactional
    public void resetPassword(String username, String newPassword) {
        if (newPassword == null || newPassword.trim().length() < 6) throw new ValidationException("密码过短");
        SystemUser user = systemUserRepository.findByUsername(username)
            .orElseThrow(() -> new ValidationException("用户名不存在"));
        user.setPassword(passwordEncoder.encode(newPassword));
        systemUserRepository.save(user);
    }
}