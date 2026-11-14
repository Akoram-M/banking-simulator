package com.banksimulator.notificationservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;


public record AccountTransferEvent(
    UUID id,
    String fromAccount,
    String toAccount,
    BigDecimal amount,
    @JsonSerialize(using = InstantSerializer.class)
    Instant timestamp
) {
    
}
