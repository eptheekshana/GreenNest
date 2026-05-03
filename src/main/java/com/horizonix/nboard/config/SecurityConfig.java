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
import org.springframework.http.HttpMethod;
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
                        // Explicitly permit all methods for authentication pages
                        .requestMatchers(HttpMethod.GET, "/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                        .requestMatchers("/", "/signup", "/registration-success", "/verify-email", "/register/test", "/properties", "/property/**", "/property-details/**", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
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

                // --- 1.5. CSRF CONFIGURATION ---
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints (they use JWT)
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

                // --- 3.5. EXCEPTION HANDLING ---
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Redirect to login page for HTML requests
                            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                response.sendError(401, "Unauthorized");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
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