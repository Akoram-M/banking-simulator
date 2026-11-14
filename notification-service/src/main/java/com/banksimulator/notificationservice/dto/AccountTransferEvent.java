package com.banksimulator.notificationservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountTransferEvent(
    UUID id,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    Instant timestamp
) {
    
}
