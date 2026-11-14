package com.bank.simulator.accountservice.service;

import java.math.BigDecimal;

import com.bank.simulator.accountservice.domain.Account;

public interface IAccountService {
    
    Account createAccount(String ownerName, BigDecimal initialBalance);

    void processTransfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws IllegalArgumentException;
}
