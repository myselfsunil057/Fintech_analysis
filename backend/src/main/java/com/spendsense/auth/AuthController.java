package com.spendsense.auth;

import com.spendsense.user.AppUser;
import com.spendsense.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users; private final PasswordEncoder passwords; private final AuthenticationManager authManager;
  public AuthController(UserRepository users,PasswordEncoder passwords,AuthenticationManager authManager){this.users=users;this.passwords=passwords;this.authManager=authManager;}
  public record Registration(@NotBlank @Size(max=120) String name,@Email @NotBlank String email,@Size(min=8,max=72) String password) {}
  public record Login(@Email @NotBlank String email,@NotBlank String password) {}
  @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
  public Map<String,String> register(@Valid @RequestBody Registration body){String email=body.email().trim().toLowerCase();if(users.findByEmailIgnoreCase(email).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists.");users.save(new AppUser(body.name().trim(),email,passwords.encode(body.password())));return Map.of("message","Account created. You can now sign in.");}
  @PostMapping("/login") public Map<String,String> login(@Valid @RequestBody Login body,HttpServletRequest request){Authentication result=authManager.authenticate(new UsernamePasswordAuthenticationToken(body.email().trim().toLowerCase(),body.password()));SecurityContext context=SecurityContextHolder.createEmptyContext();context.setAuthentication(result);SecurityContextHolder.setContext(context);request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,context);return Map.of("message","Signed in.");}
  @GetMapping("/me") public Map<String,String> me(Authentication auth){return Map.of("email",auth.getName());}
}
