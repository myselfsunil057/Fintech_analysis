package com.spendsense.transaction;

import com.spendsense.account.Account;
import com.spendsense.user.AppUser;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;

@Entity @Table(name="transactions",indexes=@Index(name="idx_tx_user_date",columnList="user_id,transaction_date"))
public class FinancialTransaction {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="user_id",nullable=false) private AppUser user;
  @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TransactionType type;
  @Column(nullable=false,precision=15,scale=2) private BigDecimal amount;
  @Column(length=60) private String category;
  @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="source_account_id",nullable=false) private Account sourceAccount;
  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="destination_account_id") private Account destinationAccount;
  @Column(nullable=false,length=240) private String description;
  @Column(name="payment_method",nullable=false,length=40) private String paymentMethod;
  @Column(name="transaction_date",nullable=false) private LocalDate transactionDate;
  @Column(name="created_at",insertable=false,updatable=false) private Instant createdAt;
  @Column(name="updated_at",insertable=false,updatable=false) private Instant updatedAt;
  protected FinancialTransaction(){}
  public FinancialTransaction(AppUser user,TransactionType type,BigDecimal amount,String category,Account source,Account destination,String description,String paymentMethod,LocalDate date){this.user=user;this.type=type;this.amount=amount;this.category=category;this.sourceAccount=source;this.destinationAccount=destination;this.description=description;this.paymentMethod=paymentMethod;this.transactionDate=date;}
  public Long getId(){return id;} public TransactionType getType(){return type;} public BigDecimal getAmount(){return amount;} public String getCategory(){return category;} public Account getSourceAccount(){return sourceAccount;} public Account getDestinationAccount(){return destinationAccount;} public String getDescription(){return description;} public String getPaymentMethod(){return paymentMethod;} public LocalDate getTransactionDate(){return transactionDate;}
  public void revise(TransactionType type,BigDecimal amount,String category,Account source,Account destination,String description,String paymentMethod,LocalDate date){this.type=type;this.amount=amount;this.category=category;this.sourceAccount=source;this.destinationAccount=destination;this.description=description;this.paymentMethod=paymentMethod;this.transactionDate=date;}
}
