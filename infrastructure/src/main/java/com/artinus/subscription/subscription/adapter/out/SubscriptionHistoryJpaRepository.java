package com.artinus.subscription.subscription.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface SubscriptionHistoryJpaRepository
        extends JpaRepository<SubscriptionHistoryJpaEntity, Long> {

    List<SubscriptionHistoryJpaEntity> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
