package com.spendsense.account;

import com.spendsense.transaction.FinancialTransaction;
import com.spendsense.transaction.TransactionRepository;
import com.spendsense.transaction.TransactionType;
import com.spendsense.user.AppUser;
import com.spendsense.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/accounts")
public class AccountController {
  private final AccountRepository accounts; private final TransactionRepository transactions; private final UserRepository users;
  public AccountController(AccountRepository accounts,TransactionRepository transactions,UserRepository users){this.accounts=accounts;this.transactions=transactions;this.users=users;}
  public record CreateAccount(@NotBlank @Size(max=100) String name,@NotBlank String type,@NotNull @DecimalMin("0.00") BigDecimal openingBalance){}
  public record AccountView(Long id,String name,String type,String currency,BigDecimal openingBalance,BigDecimal balance){}
  private AppUser owner(Authentication auth){return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
  @GetMapping @Transactional(readOnly=true) public List<AccountView> list(Authentication auth){var user=owner(auth);var all=transactions.findAllByUserIdOrderByTransactionDateDescIdDesc(user.getId());return accounts.findAllByUserIdOrderById(user.getId()).stream().map(a->{BigDecimal balance=a.getOpeningBalance();for(FinancialTransaction t:all){if(t.getSourceAccount().getId().equals(a.getId())){if(t.getType()==TransactionType.INCOME)balance=balance.add(t.getAmount());else balance=balance.subtract(t.getAmount());}if(t.getType()==TransactionType.TRANSFER&&t.getDestinationAccount()!=null&&t.getDestinationAccount().getId().equals(a.getId()))balance=balance.add(t.getAmount());}return new AccountView(a.getId(),a.getName(),a.getType(),a.getCurrency(),a.getOpeningBalance(),balance);}).toList();}
  @PostMapping @ResponseStatus(HttpStatus.CREATED) public AccountView create(@Valid @RequestBody CreateAccount input,Authentication auth){var user=owner(auth);var type=input.type().trim().toUpperCase();if(!List.of("CASH","SAVINGS","OTHER").contains(type))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account type must be CASH, SAVINGS, or OTHER.");if(input.openingBalance().scale()>2)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Opening balance can have at most two decimal places.");var saved=accounts.save(new Account(user,input.name().trim(),type,input.openingBalance()));return new AccountView(saved.getId(),saved.getName(),saved.getType(),saved.getCurrency(),saved.getOpeningBalance(),saved.getOpeningBalance());}
}
