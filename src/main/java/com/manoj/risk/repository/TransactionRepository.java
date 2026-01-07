package com.manoj.risk.repository;

import com.manoj.risk.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);
    Page<Transaction> findByTimestampBetween(Instant start, Instant end, Pageable pageable);
    Page<Transaction> findByTimestampAfter(Instant start, Pageable pageable);
    Page<Transaction> findByTimestampBefore(Instant end, Pageable pageable);
    Page<Transaction> findByCustomerIdAndTimestampBetween(String customerId, Instant start, Instant end, Pageable pageable);
    Page<Transaction> findByCustomerIdAndTimestampAfter(String customerId, Instant start, Pageable pageable);
    Page<Transaction> findByCustomerIdAndTimestampBefore(String customerId, Instant end, Pageable pageable);
}
