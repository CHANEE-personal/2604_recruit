package com.artinus.subscription.member.application.port.out;

import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.domain.SubscriptionStatus;

public interface SaveMemberPort {

    Member save(Member member);

    Member updateStatus(String phoneNumber, SubscriptionStatus newStatus);
}
