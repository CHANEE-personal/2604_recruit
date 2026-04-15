package com.artinus.subscription.subscription.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubscriptionHistory {

    private final Long id;
    private final Long memberId;
    private final String phoneNumber;
    private final Long channelId;
    private final String channelName;
    private final SubscriptionStatus previousStatus;
    private final SubscriptionStatus newStatus;
    private final LocalDateTime createdAt;
}
