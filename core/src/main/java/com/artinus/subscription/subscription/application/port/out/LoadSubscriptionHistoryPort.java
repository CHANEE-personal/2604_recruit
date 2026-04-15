package com.artinus.subscription.subscription.application.port.out;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;

import java.util.List;

public interface LoadSubscriptionHistoryPort {

    List<SubscriptionHistory> findByPhoneNumber(String phoneNumber);
}
