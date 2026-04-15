package com.artinus.subscription.member.application.port.out;

import com.artinus.subscription.member.domain.Member;

import java.util.Optional;

public interface LoadMemberPort {

    Optional<Member> findByPhoneNumber(String phoneNumber);
}
