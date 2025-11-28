package com.campus.exchange.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//the @Configuration annotation tell spring that this class contains definitions for beans and to look out for them
//@EnableWebSecurity annotation turns on the spring security and allows to change the default settings as it is done below
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //main security config: allow all requests, no login required(spring security auto generated the login page, this is to avoid that)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        //csrf: cross site request forgery, no need for our restapi
        //permitAll() allows all endpoints to be accessed without logging in.
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/claims", "api/items/**", "/api/notifications/**").permitAll()
                        .anyRequest().permitAll()).formLogin(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    //password encoder from Spring Security Dependency
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
