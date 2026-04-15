package com.artinus.subscription.subscription.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubscriptionHistoryEvent {

    private final SubscriptionHistory history;
}
