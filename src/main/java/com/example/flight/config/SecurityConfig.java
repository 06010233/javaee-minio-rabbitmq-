// 文件路径: com/example/flight/config/SecurityConfig.java

package com.example.flight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.flight.service.SystemUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SystemUserService systemUserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(systemUserService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 禁用CSRF，适用于API和前后端分离应用
            .authorizeHttpRequests(auth -> auth
                // ★★★ 关键修改 1: 明确允许对 WebSocket 端点的所有请求 ★★★
                // 这将解决 "Invalid SockJS path" 的问题，确保安全框架不拦截WebSocket连接。
                .requestMatchers("/ws/**").permitAll()

                // ★★★ 关键修改 2: 明确允许所有API和支付确认页面的访问 ★★★
                // 为了开发方便，我们暂时允许所有 /api/** 和 /payment/** 的访问
                .requestMatchers("/api/**", "/payment/**").permitAll()

                // ★★★ 关键修改 3: 其他所有请求都需要认证 (这是更安全的默认设置) ★★★
                // 当您完成开发后，可以收紧上面的规则，并使用此规则作为兜底。
                // 在当前调试阶段，使用 .anyRequest().permitAll() 也是可以的。
                .anyRequest().authenticated()
            )
            // 使用基于 Session 的认证
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // 将我们自定义的认证提供者加入配置
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}