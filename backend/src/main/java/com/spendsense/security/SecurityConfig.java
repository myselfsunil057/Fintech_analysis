package com.spendsense.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
  @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/register", "/api/auth/login", "/api/csrf", "/api/health").permitAll().anyRequest().authenticated())
      .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
      .formLogin(form -> form.disable())
      .httpBasic(basic -> basic.disable())
      .logout(logout -> logout.logoutUrl("/api/auth/logout").logoutSuccessHandler((req,res,authentication) -> res.setStatus(204)))
      .build();
  }
}
