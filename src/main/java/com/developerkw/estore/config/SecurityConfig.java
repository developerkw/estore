package com.developerkw.estore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        UserDetails sarah = User.builder()
            .username("bullish")
            .password(passwordEncoder.encode("abc123"))
            .roles("ADMIN")
            .build();
        UserDetails testuser = User.builder()
            .username("testuser")
            .password(passwordEncoder.encode("qrs456"))
            .roles("CUSTOMER")
            .build();
        UserDetails testuser2 = User.builder()
            .username("testuser2")
            .password(passwordEncoder.encode("efg789"))
            .roles("CUSTOMER")
            .build();
        return new InMemoryUserDetailsManager(sarah, testuser, testuser2);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers("/product/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui.html").hasRole("ADMIN")
                    .requestMatchers("/basket/**").hasRole("CUSTOMER")
            )
            .httpBasic(Customizer.withDefaults());

        http.csrf(t -> t.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
