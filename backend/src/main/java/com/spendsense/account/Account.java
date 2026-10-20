package com.spendsense.account;

import com.spendsense.user.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name="accounts",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","name"}))
public class Account {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
  @Column(nullable=false,length=100) private String name;
  @Column(nullable=false,length=30) private String type;
  @Column(name="opening_balance",nullable=false,precision=15,scale=2) private BigDecimal openingBalance;
  @Column(nullable=false,length=3) private String currency="INR";
  @Column(name="created_at",insertable=false,updatable=false) private Instant createdAt;
  protected Account(){}
  public Account(AppUser user,String name,String type,BigDecimal openingBalance){this.user=user;this.name=name;this.type=type;this.openingBalance=openingBalance;}
  public Long getId(){return id;} public AppUser getUser(){return user;} public String getName(){return name;} public String getType(){return type;} public BigDecimal getOpeningBalance(){return openingBalance;} public String getCurrency(){return currency;}
}
