package com.artinus.subscription.subscription.application.port.out;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;

public interface SaveSubscriptionHistoryPort {

    SubscriptionHistory save(SubscriptionHistory history);
}
