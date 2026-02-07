package com.horizonix.greennest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // --- 1. PERMISSIONS ---
                .authorizeHttpRequests(auth -> auth
                        // "/secret-admin-entry" HERE SO EVERYONE CAN ACCESS IT
                        .requestMatchers("/", "/login", "/register", "/secret-admin-entry", "/properties", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        .requestMatchers("/owner/**").hasRole("OWNER") // Lock owner pages
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Lock admin pages
                        .anyRequest().authenticated()
                )

                // --- 2. LOGIN LOGIC ---
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler((request, response, authentication) -> {

                            var roles = authentication.getAuthorities();
                            String redirectUrl = "/"; // Default for Students

                            // Check if user is an OWNER
                            if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"))) {
                                redirectUrl = "/owner/dashboard";
                            }
                            // Check if user is an ADMIN
                            else if (roles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                redirectUrl = "/admin/dashboard";
                            }

                            response.sendRedirect(redirectUrl);
                        })
                )

                // --- 3. LOGOUT ---
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}