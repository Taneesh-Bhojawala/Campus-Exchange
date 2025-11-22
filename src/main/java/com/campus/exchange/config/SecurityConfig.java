package com.campus.exchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Main security config: allow all requests, no login required
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // for simple JSON APIs we usually disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // authorization rules
                .authorizeHttpRequests(auth -> auth
                        // keep signup & auth endpoints fully open
                        .requestMatchers("/api/auth/**").permitAll()
                        // you can add more public endpoints here
                        .requestMatchers("/students/**", "/items/**", "/uploads/**").permitAll()
                        // everything else also allowed (no authentication at all)
                        .anyRequest().permitAll()
                )

                // disable default login form & HTTP basic popup
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    // still keep your password encoder bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
