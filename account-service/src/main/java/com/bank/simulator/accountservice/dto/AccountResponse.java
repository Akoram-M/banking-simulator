package com.bank.simulator.accountservice.dto;

import java.math.BigDecimal;

import com.bank.simulator.accountservice.domain.Account;

public record AccountResponse(String accountNumber, String ownerName, BigDecimal balance) {
    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(account.getAccountNumber(), account.getOwnerName(), account.getBalance());
    }
}