package com.shopee.shopeecareer.Resconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class Resconfig {
    @Bean
    // Phương thức PasswordEncoder
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    // Phương thức SecurityFilterChain
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf((csrf) -> csrf.disable()) // cơ chế bảo vệ chống lại các tấn công Cross-Site Request Forgery
                .cors((cors) -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.addAllowedOrigin("http://localhost:3000"); // Cho phép tất cả các nguồn gốc (origin) gửi yêu cầu.
                    // Chỉ định các phương thức HTTP được phép (GET, POST, PUT, DELETE).
                    configuration.addAllowedMethod(HttpMethod.GET);
                    configuration.addAllowedMethod(HttpMethod.POST);
                    configuration.addAllowedMethod(HttpMethod.PUT);
                    configuration.addAllowedMethod(HttpMethod.DELETE);
                    configuration.addAllowedHeader("*"); // Cho phép tất cả các header trong yêu cầu.
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/**").permitAll().anyRequest().authenticated()
                ).build();
    }
}
