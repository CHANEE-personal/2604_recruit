package com.artinus.subscription.subscription.application.port.out;

import java.util.List;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;

public interface SummarizeHistoryPort {

    String summarizeHistory(List<SubscriptionHistory> histories);
}
