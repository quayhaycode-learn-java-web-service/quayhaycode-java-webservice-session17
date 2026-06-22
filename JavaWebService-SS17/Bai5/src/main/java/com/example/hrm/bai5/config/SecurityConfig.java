package com.example.hrm.bai5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Các endpoint công khai (Nếu có)
                        .requestMatchers(HttpMethod.GET, "/api/v1/games").permitAll()

                        // 2. Phân quyền thô dựa trên tiền tố URL (Vòng ngoài)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/moderator/**").hasAnyRole("GAME_MODERATOR", "ADMIN")

                        // 3. Quy tắc nghiệp vụ: Phải đăng nhập mới được thao tác các tính năng khác
                        .requestMatchers("/api/v1/players/**").hasAnyRole("PLAYER", "GAME_MODERATOR", "ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}