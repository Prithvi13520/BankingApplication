package com.app.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.banking.entity.Transaction;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction,String>{

    List<Transaction> findByAccountNumber(String accountNumber);

}
