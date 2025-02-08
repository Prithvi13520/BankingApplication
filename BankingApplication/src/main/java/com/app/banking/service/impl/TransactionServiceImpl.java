package com.app.banking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.app.banking.constants.Constants;
import com.app.banking.dto.TransactionDto;
import com.app.banking.entity.Transaction;
import com.app.banking.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService{

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Async
    public void saveTransaction(TransactionDto transactionDto) {
        logger.info("Save transaction started");
        Transaction transaction = Transaction.builder()
        .transactionType(transactionDto.getTransactionType())
        .accountNumber(transactionDto.getAccountNumber())
        .amount(transactionDto.getAmount())
        .status(Constants.SUCCESS)
        .build();

        transactionRepository.save(transaction);
        logger.info("Transaction saved successfully");
    }

}
