// 文件路径: src/main/java/com/example/flight/controller/AuthController.java
package com.example.flight.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flight.controller.dto.ErrorResponse;
import com.example.flight.controller.dto.LoginRequest;
import com.example.flight.controller.dto.RegisterRequest;
import com.example.flight.entity.SystemUser;
import com.example.flight.exception.ValidationException;
import com.example.flight.service.SystemUserService; // 注意这里不需要 PassengerService 了，因为逻辑封装进 SystemUserService 了

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SystemUserService systemUserService;
    private final AuthenticationManager authenticationManager;

    // --- DTO for password reset ---
    public static class PasswordResetValidationRequest {
        private String username; private String idCard; private String phone;
        public String getUsername() { return username; }
        public String getIdCard() { return idCard; }
        public String getPhone() { return phone; }
    }

    public static class PasswordResetRequest {
        private String username; private String newPassword;
        public String getUsername() { return username; }
        public String getNewPassword() { return newPassword; }
    }

    @Autowired
    public AuthController(SystemUserService systemUserService,
                          AuthenticationManager authenticationManager) {
        this.systemUserService = systemUserService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            SystemUser user = systemUserService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
            systemUserService.updateLastLoginTransactional(user.getUserId());
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误"), HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "认证失败: " + e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部发生未知错误"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- ★★★ 核心修改：使用 Service 层封装的注册逻辑 ★★★ ---
  // --- 修改后的注册接口 ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // 1. 调用 Service 注册 (事务在 Service 内部完成并提交)
            SystemUser savedUser = systemUserService.registerNewUser(registerRequest);
            
            // 2. ★★★ 在这里清除密码 ★★★
            // 此时事务已提交，修改对象属性只影响返回的 JSON，不会影响数据库
            savedUser.setPassword(null);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(500, "注册失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "用户未登录"), HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();
        SystemUser user = systemUserService.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return ResponseEntity.ok("登出成功");
    }

    @PostMapping("/validate-reset-request")
    public ResponseEntity<?> validatePasswordResetRequest(@RequestBody PasswordResetValidationRequest req) {
        try {
            systemUserService.validateResetRequest(req.username, req.idCard, req.phone);
            return ResponseEntity.ok(Map.of("message", "Validation successful."));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest req) {
        try {
            systemUserService.resetPassword(req.username, req.newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successful."));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(400, e.getMessage()));
        }
    }
}