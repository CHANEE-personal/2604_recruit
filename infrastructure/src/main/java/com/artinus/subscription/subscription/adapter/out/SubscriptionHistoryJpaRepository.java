package com.artinus.subscription.subscription.adapter.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface SubscriptionHistoryJpaRepository
        extends JpaRepository<SubscriptionHistoryJpaEntity, Long> {

    List<SubscriptionHistoryJpaEntity> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
