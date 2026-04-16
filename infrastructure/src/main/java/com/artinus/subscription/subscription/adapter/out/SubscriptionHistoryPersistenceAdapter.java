package com.artinus.subscription.subscription.adapter.out;

import java.util.List;

import org.springframework.stereotype.Component;

import com.artinus.subscription.subscription.application.port.out.LoadSubscriptionHistoryPort;
import com.artinus.subscription.subscription.application.port.out.SaveSubscriptionHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class SubscriptionHistoryPersistenceAdapter
        implements LoadSubscriptionHistoryPort, SaveSubscriptionHistoryPort {

    private final SubscriptionHistoryJpaRepository subscriptionHistoryJpaRepository;
    private final SubscriptionHistoryMapper subscriptionHistoryMapper;


    public SubscriptionHistory save(SubscriptionHistory history) {
        SubscriptionHistoryJpaEntity entity = subscriptionHistoryMapper.toEntity(history);
        return subscriptionHistoryMapper.toDomain(subscriptionHistoryJpaRepository.save(entity));
    }


    public List<SubscriptionHistory> findByPhoneNumber(String phoneNumber) {
        return subscriptionHistoryJpaRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .stream()
                .map(subscriptionHistoryMapper::toDomain)
                .toList();
    }
}
