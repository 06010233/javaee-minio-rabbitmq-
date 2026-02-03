package com.example.flight;

import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling; // ★ 新增导入
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.entity.Passenger;
import com.example.flight.entity.SystemUser;
import com.example.flight.repository.PassengerRepository;
import com.example.flight.service.SystemUserService;

@SpringBootApplication
// @EnableScheduling // ★★★ 添加此注解以启用定时任务功能 ★★★
public class FlightTicketSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightTicketSystemApplication.class, args);
    }
    
    /**
     * 应用启动时执行的数据库初始化任务。
     * 这个Bean会检查并确保关键测试用户（管理员和乘客）存在且密码正确。
     * 这是为了保证开发环境的数据一致性，避免手动操作数据库。
     */
    @Bean
    @Transactional
    CommandLineRunner initDatabase(
        SystemUserService userService, 
        PasswordEncoder passwordEncoder, 
        PassengerRepository passengerRepository
    ) {
        return args -> {
            // --- 1. 创建或更新管理员用户 ---
            createOrUpdateAdmin(userService, passwordEncoder, "admin", "password123");
            
            // --- 2. 创建或更新乘客用户 ---
            // 将乘客ID与系统用户名和密码关联起来
            createOrUpdatePassengerUser(userService, passwordEncoder, passengerRepository, "passenger1", "passenger", 1);
            createOrUpdatePassengerUser(userService, passwordEncoder, passengerRepository, "passenger2", "passenger", 2);
        };
    }

    /**
     * 私有辅助方法：创建或更新一个管理员账户
     */
    private void createOrUpdateAdmin(SystemUserService userService, PasswordEncoder passwordEncoder, String username, String rawPassword) {
        Optional<SystemUser> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            // 如果不存在，创建一个新的管理员
            System.out.printf("Admin user '%s' not found. Creating...%n", username);
            SystemUser admin = new SystemUser();
            admin.setUsername(username);
            admin.setFullName("系统管理员");
            admin.setRole(SystemUser.UserRole.ADMIN);
            admin.setPassword(passwordEncoder.encode(rawPassword));
            userService.saveSystemUserWithoutReEncoding(admin);
        } else {
            // 如果存在，检查密码是否需要更新
            SystemUser admin = userOpt.get();
            if (!passwordEncoder.matches(rawPassword, admin.getPassword())) {
                System.out.printf("Password for admin '%s' is outdated. Updating...%n", username);
                admin.setPassword(passwordEncoder.encode(rawPassword));
                userService.saveSystemUserWithoutReEncoding(admin);
            } else {
                System.out.printf("Admin user '%s' is up to date.%n", username);
            }
        }
    }

    /**
     * 私有辅助方法：创建或更新一个与乘客关联的用户账户
     */
    private void createOrUpdatePassengerUser(SystemUserService userService, PasswordEncoder passwordEncoder, PassengerRepository passengerRepository, String username, String rawPassword, Integer passengerId) {
        // 检查关联的乘客是否存在
        Optional<Passenger> passengerOpt = passengerRepository.findById(passengerId);
        if (passengerOpt.isEmpty()) {
            System.out.printf("Cannot create user '%s' because Passenger with ID %d does not exist.%n", username, passengerId);
            return;
        }
        Passenger passenger = passengerOpt.get();

        Optional<SystemUser> userOpt = userService.findByUsername(username);
        if (userOpt.isEmpty()) {
            // 如果不存在，创建一个新的乘客用户
            System.out.printf("Passenger user '%s' not found. Creating...%n", username);
            SystemUser newUser = new SystemUser();
            newUser.setUsername(username);
            newUser.setFullName(passenger.getName()); // 使用乘客姓名作为全名
            newUser.setRole(SystemUser.UserRole.PASSENGER);
            newUser.setPassenger(passenger);
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            userService.saveSystemUserWithoutReEncoding(newUser);
        } else {
            // 如果存在，检查密码是否需要更新
            SystemUser existingUser = userOpt.get();
            if (!passwordEncoder.matches(rawPassword, existingUser.getPassword())) {
                System.out.printf("Password for passenger user '%s' is outdated. Updating...%n", username);
                existingUser.setPassword(passwordEncoder.encode(rawPassword));
                userService.saveSystemUserWithoutReEncoding(existingUser);
            } else {
                System.out.printf("Passenger user '%s' is up to date.%n", username);
            }
        }
    }
}