package com.artinus.subscription.member.application.port.out;

import java.util.Optional;

import com.artinus.subscription.member.domain.Member;

public interface LoadMemberPort {

    Optional<Member> findByPhoneNumber(String phoneNumber);
}
