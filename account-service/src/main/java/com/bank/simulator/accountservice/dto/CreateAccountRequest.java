package com.bank.simulator.accountservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateAccountRequest(
    @NotBlank(message = "Owner name cannot be blank") 
    String ownerName, 
    
    @NotNull 
    @PositiveOrZero(message = "Initial balance cannot be negative") 
    BigDecimal initialBalance
) {}
