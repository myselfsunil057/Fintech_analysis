package com.spendsense.auth;

import com.spendsense.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class UserDetailsServiceImpl {
  @Bean UserDetailsService userDetailsService(UserRepository users){return email->users.findByEmailIgnoreCase(email).map(u->User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles("USER").build()).orElseThrow(()->new UsernameNotFoundException("Account not found"));}
  @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{return configuration.getAuthenticationManager();}
}
