package com.banksimulator.notificationservice.listner;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.banksimulator.notificationservice.dto.AccountTransferEvent;

@Service
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final StringRedisTemplate redisTemplate;

    public NotificationListener(StringRedisTemplate template){
        this.redisTemplate = template;
    }
    /**
     * -----------------------------------------------------------------------
     * PATTERN: Event-Driven Consumer
     * -----------------------------------------------------------------------
     * This service is completely decoupled from the Account Service database.
     * It only knows that a transfer happened, and responds by taking action.
     * -----------------------------------------------------------------------
     */
    @KafkaListener(topics = "transactions.events", groupId = "notification-group")
    public void handleTransferEvent(AccountTransferEvent event) {

        String eventId = event.id().toString();

        // setIfAbsent acts as a distributed lock.
        // It returns true if the key didn't exist (first time seeing the event).
        Boolean isNewEvent = redisTemplate.opsForValue().setIfAbsent(eventId, "processed", Duration.ofHours(24));

        if (Boolean.FALSE.equals(isNewEvent)) {
            log.warn("⚠️ IDEMPOTENCY LOCK: Dropped duplicate event [{}]", eventId);
            return;
        }

        log.info("🔔 NOTIFICATION: Processed Transfer Event [{}] - Amount: ${} from {} to {}",
                eventId, event.amount(), event.fromAccount(), event.toAccount());
    }
}