package com.artinus.subscription.subscription.application.port.in;

import com.artinus.subscription.subscription.domain.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public interface GetSubscriptionHistoryQuery {

    HistoryResponse getHistory(String phoneNumber);

    @Getter
    @Builder
    class HistoryResponse {

        private final List<HistoryItem> history;
        private final String summary;
    }


    @Getter
    @Builder
    class HistoryItem {

        private final Long id;
        private final String channelName;
        private final LocalDateTime date;
        private final SubscriptionStatus previousStatus;
        private final SubscriptionStatus subscriptionStatus;
    }
}
