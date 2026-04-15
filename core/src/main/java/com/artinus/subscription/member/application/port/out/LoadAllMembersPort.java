package com.artinus.subscription.member.application.port.out;

import com.artinus.subscription.member.domain.Member;

import java.util.List;

public interface LoadAllMembersPort {

    List<Member> findAll();
}
