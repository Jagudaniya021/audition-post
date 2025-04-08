/*
 * Copyright 2025 XYZ, Inc.
 *  Licensed under the XYZ License, Version 1.0
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.xyz.org/licenses/LICENSE-1.0
 */

package com.audition.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Spring security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ALLOWED_URLS = {"/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**",
        "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**",
        "/webjars/**", "/swagger-ui.html", "/api/auth/**", "/api/test/**", "/authenticate"};
    private final transient CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${spring.security.user.username}")
    private transient String username;

    @Value("${spring.security.user.password}")
    private transient String password;

    @Value("${spring.security.user.role}")
    private transient String role;

    public SecurityConfig(final CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    /**
     * Security filter chain.
     *
     * @param http - http security.
     * @return SecurityFilterChain - with custom entry point.
     * @throws Exception - exception.
     */
    @Bean
    @SuppressWarnings("PMD")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable())
            .authorizeRequests(auth -> auth
                .requestMatchers(ALLOWED_URLS).permitAll()
                .anyRequest().authenticated())
            .httpBasic()
            .authenticationEntryPoint(customAuthenticationEntryPoint);

        return http.build();
    }

    /**
     * user details for authentication.
     *
     * @return user details for authentication.
     */
    @Bean
    public UserDetailsService users() {
        final UserDetails user = User.builder().username(username).password(passwordEncoder().encode(password))
            .roles(role).build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * password encoder using BCrypt hashing algorithm.
     *
     * @return password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
