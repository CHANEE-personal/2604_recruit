package com.artinus.subscription.member.application.port.in;

import java.util.List;

import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.Builder;
import lombok.Getter;

public interface GetMemberQuery {

    List<MemberResponse> getMembers();

    @Getter
    @Builder
    class MemberResponse {

        private final Long id;
        private final String phoneNumber;
        private final SubscriptionStatus subscriptionStatus;


        public static MemberResponse from(Member member) {
            return MemberResponse.builder()
                    .id(member.getId())
                    .phoneNumber(member.getPhoneNumber())
                    .subscriptionStatus(member.getSubscriptionStatus())
                    .build();
        }
    }
}
