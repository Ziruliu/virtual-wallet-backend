package com.ziruliu.backend.repository;

import com.ziruliu.backend.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
