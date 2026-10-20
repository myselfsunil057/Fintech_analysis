package com.spendsense.transaction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<FinancialTransaction,Long> { List<FinancialTransaction> findAllByUserIdOrderByTransactionDateDescIdDesc(Long userId); Optional<FinancialTransaction> findByIdAndUserId(Long id,Long userId); }
