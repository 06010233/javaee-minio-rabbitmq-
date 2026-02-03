package com.example.flight.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flight.controller.dto.RegisterRequest; 
import com.example.flight.entity.Passenger;
import com.example.flight.entity.SystemUser;
import com.example.flight.exception.ValidationException;
import com.example.flight.service.SystemUserService;

@RestController
@RequestMapping("/api/system-users")
public class SystemUserController {

    private final SystemUserService systemUserService;

    @Autowired
    public SystemUserController(SystemUserService systemUserService) {
        this.systemUserService = systemUserService;
    }

    // --- 基础 CRUD ---

    @GetMapping
    public List<SystemUser> getAllSystemUsers() { 
        return systemUserService.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<SystemUser> getSystemUserById(@PathVariable Integer id) { 
        return systemUserService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }
    
    // --- 更新用户信息 (包含改密逻辑) ---
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSystemUser(@PathVariable Integer id, @RequestBody SystemUser systemUserUpdateData) {
        Optional<SystemUser> existingUserOpt = systemUserService.findById(id);
        if (existingUserOpt.isEmpty()) return ResponseEntity.notFound().build();
        
        SystemUser existingUser = existingUserOpt.get();
        boolean accountInfoChanged = false;
        
        // 更新用户名
        String newUsername = systemUserUpdateData.getUsername();
        if (newUsername != null && !newUsername.isEmpty() && !newUsername.equals(existingUser.getUsername())) {
            if (systemUserService.existsByUsername(newUsername)) return ResponseEntity.badRequest().body("用户名已存在");
            existingUser.setUsername(newUsername);
            accountInfoChanged = true;
        }
        
        // 更新密码
        String newPassword = systemUserUpdateData.getPassword();
        if (newPassword != null && !newPassword.isEmpty()) {
            existingUser.setPassword(newPassword);
            systemUserService.saveSystemUser(existingUser); // 这里的 save 会加密密码
            accountInfoChanged = true;
        } else {
            systemUserService.saveSystemUserWithoutReEncoding(existingUser);
        }
        
        // 如果关键信息变更，更新 SecurityContext
        if (accountInfoChanged) {
            UsernamePasswordAuthenticationToken newAuthenticationToken = new UsernamePasswordAuthenticationToken(
                existingUser.getUsername(), null, systemUserService.loadUserByUsername(existingUser.getUsername()).getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuthenticationToken);
        }
        
        SystemUser finalUser = systemUserService.findByUsername(existingUser.getUsername()).get();
        finalUser.setPassword(null);
        return ResponseEntity.ok(finalUser);
    }

    // --- 常用乘机人接口 ---

    /**
     * 获取指定用户的常用乘机人列表
     */
    @GetMapping("/{userId}/contacts")
    public ResponseEntity<List<Passenger>> getContacts(@PathVariable Integer userId) {
        try {
            List<Passenger> contacts = systemUserService.getMyContacts(userId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 添加常用乘机人
     */
    @PostMapping("/{userId}/contacts")
    public ResponseEntity<?> addContact(@PathVariable Integer userId, @RequestBody RegisterRequest contactInfo) {
        try {
            systemUserService.addContact(userId, contactInfo);
            return ResponseEntity.ok(Map.of("message", "常用乘机人添加成功"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "添加失败"));
        }
    }

    /**
     * 删除常用乘机人
     */
    @DeleteMapping("/{userId}/contacts/{passengerId}")
    public ResponseEntity<?> removeContact(@PathVariable Integer userId, @PathVariable Integer passengerId) {
        try {
            systemUserService.removeContact(userId, passengerId);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "删除失败"));
        }
    }

    // --- ★★★ 重置密码接口 (已更新为三要素验证) ★★★ ---

    /**
     * 重置密码接口
     * 接收 JSON: { "username": "...", "idCard": "...", "phone": "..." }
     * 逻辑：验证 用户名+身份证+手机号，成功后重置密码为 123456
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String idCard = params.get("idCard");
        String phone = params.get("phone");

        // 1. 检查参数是否完整
        if (username == null || idCard == null || phone == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "请填写完整信息（用户名、身份证、手机号）"));
        }

        try {
            // 2. 调用 Service 层进行三要素验证
            // 注意：请确保 SystemUserService.java 中的 validateResetRequest 方法接受这三个参数
            systemUserService.validateResetRequest(username, idCard, phone);

            // 3. 验证通过，重置密码为默认值 "123456"
            String defaultPassword = "123456";
            systemUserService.resetPassword(username, defaultPassword);

            return ResponseEntity.ok(Map.of("message", "验证通过！密码已重置为: " + defaultPassword + "，请尽快登录修改。"));

        } catch (ValidationException e) {
            // 捕获 Service 层抛出的验证错误（如：信息不匹配）
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "系统内部错误"));
        }
    }
}