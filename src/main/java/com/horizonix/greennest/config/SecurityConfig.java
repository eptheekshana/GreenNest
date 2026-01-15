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
                // CSRF Protection
                // For this assignment, we will keep it disabled to make Postman testing easier initially.
                .csrf(csrf -> csrf.disable())

                // Authorize Requests
                .authorizeHttpRequests(auth -> auth
                        // Allow everyone to see these pages (Public)
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // Restrict Admin pages to ADMIN role only
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Restrict Owner pages to OWNER role only
                        .requestMatchers("/owner/**").hasRole("OWNER")

                        // All other pages require a logged-in user
                        .anyRequest().authenticated()
                )

                // Form Login Configuration
                .formLogin(form -> form
                        .loginPage("/login") // Logic: If unauthenticated, redirect here
                        .defaultSuccessUrl("/", true) // Redirect here after successful login
                        .permitAll()
                )

                // Logout Configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Redirect here after logout
                        .permitAll()
                );

        return http.build();
    }

    // This Bean handles password encryption
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}