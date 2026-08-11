package com.cloudweb.cloud.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Value("${security.login.page}")
    private String loginPage;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests().requestMatchers("/**").permitAll()
                .anyRequest().authenticated().and().formLogin().loginPage(loginPage) // Specify the custom login page URL
                .permitAll()
                .and()
                .logout()
                .permitAll();
        http.headers().contentTypeOptions().disable();
        return http.build();
    }

}

