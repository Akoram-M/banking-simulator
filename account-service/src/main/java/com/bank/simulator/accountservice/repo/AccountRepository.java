package com.bank.simulator.accountservice.repo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.simulator.accountservice.domain.Account;

public interface AccountRepository extends JpaRepository<Account, UUID>{
    
    Optional<Account> findByAccountNumber(String accountNumber);
}
