package com.spendsense.transaction;

import com.spendsense.account.Account;
import com.spendsense.account.AccountRepository;
import com.spendsense.user.AppUser;
import com.spendsense.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionRepository transactions; private final AccountRepository accounts; private final UserRepository users;
  public TransactionController(TransactionRepository transactions,AccountRepository accounts,UserRepository users){this.transactions=transactions;this.accounts=accounts;this.users=users;}
  public record SaveTransaction(@NotNull TransactionType type,@NotNull @DecimalMin(value="0.01") BigDecimal amount,@Size(max=60) String category,@NotNull Long sourceAccountId,Long destinationAccountId,@NotBlank @Size(max=240) String description,@NotBlank @Size(max=40) String paymentMethod,@NotNull LocalDate transactionDate){}
  public record TransactionView(Long id,TransactionType type,BigDecimal amount,String category,Long sourceAccountId,String sourceAccount,Long destinationAccountId,String destinationAccount,String description,String paymentMethod,LocalDate transactionDate){}
  private AppUser owner(Authentication auth){return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));}
  private Account account(Long id,Long userId){return accounts.findByIdAndUserId(id,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Account is not available to this user."));}
  private void validate(SaveTransaction input){if(input.amount().scale()>2)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Amount can have at most two decimal places.");if(input.type()==TransactionType.TRANSFER&&(input.destinationAccountId()==null||input.destinationAccountId().equals(input.sourceAccountId())))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A transfer needs two different accounts.");if(input.type()!=TransactionType.TRANSFER&&input.destinationAccountId()!=null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Destination account is only valid for transfers.");if(input.type()==TransactionType.TRANSFER&&input.category()!=null&&!input.category().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Transfers cannot have an expense category.");}
  private TransactionView view(FinancialTransaction t){return new TransactionView(t.getId(),t.getType(),t.getAmount(),t.getCategory(),t.getSourceAccount().getId(),t.getSourceAccount().getName(),t.getDestinationAccount()==null?null:t.getDestinationAccount().getId(),t.getDestinationAccount()==null?null:t.getDestinationAccount().getName(),t.getDescription(),t.getPaymentMethod(),t.getTransactionDate());}
  @GetMapping @Transactional(readOnly=true) public List<TransactionView> list(Authentication auth){var user=owner(auth);return transactions.findAllByUserIdOrderByTransactionDateDescIdDesc(user.getId()).stream().map(this::view).toList();}
  @PostMapping @ResponseStatus(HttpStatus.CREATED) @Transactional public TransactionView create(@Valid @RequestBody SaveTransaction input,Authentication auth){validate(input);var user=owner(auth);var source=account(input.sourceAccountId(),user.getId());var destination=input.destinationAccountId()==null?null:account(input.destinationAccountId(),user.getId());return view(transactions.save(new FinancialTransaction(user,input.type(),input.amount(),input.category(),source,destination,input.description().trim(),input.paymentMethod().trim(),input.transactionDate())));}
  @PutMapping("/{id}") @Transactional public TransactionView update(@PathVariable Long id,@Valid @RequestBody SaveTransaction input,Authentication auth){validate(input);var user=owner(auth);var tx=transactions.findByIdAndUserId(id,user.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));var source=account(input.sourceAccountId(),user.getId());var destination=input.destinationAccountId()==null?null:account(input.destinationAccountId(),user.getId());tx.revise(input.type(),input.amount(),input.category(),source,destination,input.description().trim(),input.paymentMethod().trim(),input.transactionDate());return view(tx);}
  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) @Transactional public void delete(@PathVariable Long id,Authentication auth){var user=owner(auth);var tx=transactions.findByIdAndUserId(id,user.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));transactions.delete(tx);}
}
