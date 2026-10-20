package com.spendsense.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="users")
public class AppUser {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="full_name", nullable=false, length=120) private String fullName;
  @Column(nullable=false, unique=true, length=254) private String email;
  @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
  @Column(name="created_at", insertable=false, updatable=false) private Instant createdAt;
  protected AppUser() {}
  public AppUser(String fullName,String email,String passwordHash){this.fullName=fullName;this.email=email;this.passwordHash=passwordHash;}
  public Long getId(){return id;} public String getFullName(){return fullName;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;}
}
