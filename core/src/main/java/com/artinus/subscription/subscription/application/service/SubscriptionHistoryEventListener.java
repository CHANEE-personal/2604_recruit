package com.artinus.subscription.subscription.application.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.artinus.subscription.subscription.application.port.out.SaveSubscriptionHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistoryEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class SubscriptionHistoryEventListener {

    private final SaveSubscriptionHistoryPort saveSubscriptionHistoryPort;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(SubscriptionHistoryEvent event) {
        try {
            saveSubscriptionHistoryPort.save(event.getHistory());
        } catch(Exception e) {
            log.error("Failed to save subscription history: phoneNumber={}, channel={}, {} -> {}",
                    event.getHistory()
                            .getPhoneNumber(), event.getHistory()
                            .getChannelName(), event.getHistory()
                            .getPreviousStatus(), event.getHistory()
                            .getNewStatus(), e);
        }
    }
}
