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
                // CSRF Protection (Disabled for development ease)
                .csrf(csrf -> csrf.disable())

                // --- 1. DEFINE WHO CAN SEE WHAT ---
                .authorizeHttpRequests(auth -> auth
                        // Public Pages (Home, Login, Register, Search, Verification)
                        .requestMatchers("/", "/register", "/login", "/verify-otp", "/properties", "/search", "/property/**").permitAll()

                        // Static Resources (CSS, Images)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // Restrict Admin pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Restrict Owner pages
                        .requestMatchers("/owner/**").hasRole("OWNER")

                        // Everything else requires login
                        .anyRequest().authenticated()
                )

                // --- 2. LOGIN CONFIGURATION ---
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticateTheUser") // Ensure your login form uses th:action="@{/authenticateTheUser}" or just "/login" (POST)

                        // ✅ CUSTOM SUCCESS HANDLER (The Logic You Needed)
                        .successHandler((request, response, authentication) -> {
                            var roles = authentication.getAuthorities().stream()
                                    .map(r -> r.getAuthority()).toList();

                            if (roles.contains("ROLE_ADMIN")) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (roles.contains("ROLE_OWNER")) {
                                response.sendRedirect("/owner/dashboard");
                            } else {
                                // Students go here!
                                response.sendRedirect("/properties");
                            }
                        })
                        .permitAll()
                )

                // --- 3. LOGOUT CONFIGURATION ---
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