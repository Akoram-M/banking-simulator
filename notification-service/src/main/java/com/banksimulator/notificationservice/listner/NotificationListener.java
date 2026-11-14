package com.banksimulator.notificationservice.listner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.banksimulator.notificationservice.dto.AccountTransferEvent;

@Service
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

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
        
        // In the next phase, we will check Redis here using event.id()
        // to ensure we haven't already processed this event.
        
        log.info("🔔 NOTIFICATION: Processed Transfer Event [{}] - Amount: ${} from {} to {}",
            event.id(), 
            event.amount(), 
            event.fromAccount(), 
            event.toAccount()
        );
    }
}