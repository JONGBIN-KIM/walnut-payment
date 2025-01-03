package com.assignment.walnut.repository;

import com.assignment.walnut.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByTransactionId(String transactionId);

    Optional<Transaction> findByRefId(String refId);

    Page<Transaction> findByStoreId(String storeId, Pageable pageable);

    List<Transaction> findTop10ByStoreIdOrderByCreatedAtDesc(String storeId);

    List<Transaction> findTop10ByOrderByCreatedAtDesc();
}
