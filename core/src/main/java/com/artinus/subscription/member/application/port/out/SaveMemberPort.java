package com.artinus.subscription.member.application.port.out;

import com.artinus.subscription.member.domain.Member;

public interface SaveMemberPort {

    Member save(Member member);
}
