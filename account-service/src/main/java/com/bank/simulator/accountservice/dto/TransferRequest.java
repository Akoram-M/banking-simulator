package com.bank.simulator.accountservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
    @NotBlank String fromAccountNumber, 
    @NotBlank String toAccountNumber, 
    
    @NotNull 
    @Positive(message = "Transfer amount must be strictly positive") 
    BigDecimal amount
) {}