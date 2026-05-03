package com.horizonix.nboard.config;

import com.horizonix.nboard.security.JwtAuthenticationFilter;
import com.horizonix.nboard.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider daoAuthenticationProvider,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // --- 1. PERMISSIONS ---
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/signup", "/registration-success", "/verify-email", "/properties", "/property/**", "/property-details/**", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/user/**").hasRole("STUDENT")
                        .requestMatchers("/api/owner/**").hasRole("OWNER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/user/**").hasRole("STUDENT") // Student/Buyer pages
                        .requestMatchers("/owner/**").hasRole("OWNER") // Lock owner pages
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Lock admin pages
                        .anyRequest().authenticated()
                )

                // --- 2. LOGIN LOGIC (UPDATED) ---
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        .permitAll()
                        // CUSTOM REDIRECT LOGIC STARTS HERE
                        .successHandler((request, response, authentication) -> {

                            var roles = authentication.getAuthorities();
                            String redirectUrl = "/user/listings"; // Default for STUDENT/BUYER

                            // Check if user is an OWNER (Property Owner)
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
                )

                // --- 4. AUTHENTICATION PROVIDER ---
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}