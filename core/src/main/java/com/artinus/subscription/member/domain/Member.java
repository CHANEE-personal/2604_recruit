package com.artinus.subscription.member.domain;

import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Member {

    private final Long id;
    private final String phoneNumber;
    private final SubscriptionStatus subscriptionStatus;


    public static Member create(String phoneNumber) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();
    }


    public static Member create(String phoneNumber, SubscriptionStatus initialStatus) {
        return Member.builder()
                .phoneNumber(phoneNumber)
                .subscriptionStatus(initialStatus)
                .build();
    }
}
