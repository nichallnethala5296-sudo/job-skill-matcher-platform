package com.jobmatcher.config;

import com.jobmatcher.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/jobs", "/jobs/view/**", "/register", "/login", "/css/**", "/js/**", "/h2-console/**", "/api/**").permitAll()
                .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                .requestMatchers("/recruiter/**").hasRole("RECRUITER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", false)
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().stream().findFirst().get().getAuthority();
                    if (role.equals("ROLE_ADMIN")) {
                        response.sendRedirect("/admin/dashboard");
                    } else if (role.equals("ROLE_RECRUITER")) {
                        response.sendRedirect("/recruiter/dashboard");
                    } else {
                        response.sendRedirect("/candidate/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/error/403"));

        return http.build();
    }
}