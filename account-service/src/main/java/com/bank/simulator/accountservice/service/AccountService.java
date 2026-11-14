package com.bank.simulator.accountservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.simulator.accountservice.domain.Account;
import com.bank.simulator.accountservice.dto.AccountTransferEvent;
import com.bank.simulator.accountservice.repo.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class AccountService implements IAccountService {
    
    private final AccountRepository accountRepository;

    // Inject KafkaTemplate
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional 
    public Account createAccount(String ownerName, BigDecimal initialBalance) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException("Owner name cannot be empty.");
        }
        
        String accountNumber = generateAccountNumber(ownerName.toUpperCase());
        
        // Use the constructor provided in the entity
        Account newAccount = new Account(accountNumber, ownerName, initialBalance);
        return accountRepository.save(newAccount);
    }

    @Override
    @Transactional 
    public void processTransfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        // PATTERN: Modern Optional handling
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("From Account Not Found"));
            
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new IllegalArgumentException("To Account Not Found"));
        
        // The domain entity enforces business rules (throws exception if insufficient funds)
        fromAccount.debit(amount);
        toAccount.credit(amount);
        
        // PATTERN: JPA Dirty Checking
        // DO NOT call save() here. Hibernate tracks 'fromAccount' and 'toAccount'.
        // Upon method exit, the transaction commits and Hibernate automatically flushes the UPDATEs.

        AccountTransferEvent event = new AccountTransferEvent(UUID.randomUUID(), fromAccountNumber, toAccountNumber, amount, Instant.now());
    
        kafkaTemplate.send("transactions.events", event);
    }
    
    private String generateAccountNumber(String ownerName) {
        // String format prevents string concatenation in loops/logic
        return ownerName.charAt(0) + String.format("%08d", ThreadLocalRandom.current().nextInt(0, 100000000)) + ownerName.charAt(ownerName.length()-1);
    }
}