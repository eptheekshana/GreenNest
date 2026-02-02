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
                // Disable CSRF for easier development (enable in production)
                .csrf(csrf -> csrf.disable())

                // --- AUTHORIZATION RULES ---
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBLIC PAGES (No Login Required)
                        // Added "/register", "/verify-otp", and "/super-secret-admin-create"
                        .requestMatchers("/", "/login", "/register", "/verify-otp", "/super-secret-admin-create").permitAll()

                        // Allow static resources (CSS, Images, Uploads)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // Allow searching and viewing properties without login
                        .requestMatchers("/properties", "/search", "/property/**").permitAll()

                        // 2. PROTECTED PAGES
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/owner/**").hasRole("OWNER")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // --- LOGIN CONFIGURATION ---
                .formLogin(form -> form
                        .loginPage("/login") // Custom Login HTML
                        .loginProcessingUrl("/login") // Where the form POSTs to
                        .successHandler((request, response, authentication) -> {
                            // Custom Redirect based on Role
                            var roles = authentication.getAuthorities().stream()
                                    .map(r -> r.getAuthority()).toList();

                            if (roles.contains("ROLE_ADMIN")) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (roles.contains("ROLE_OWNER")) {
                                response.sendRedirect("/owner/dashboard");
                            } else {
                                response.sendRedirect("/properties"); // Students go here
                            }
                        })
                        .permitAll()
                )

                // --- LOGOUT CONFIGURATION ---
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