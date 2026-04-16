package com.artinus.subscription.member.application.port.out;

import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

public interface UpdateMemberStatusPort {

    void updateStatus(String phoneNumber, SubscriptionStatus newStatus);
}
