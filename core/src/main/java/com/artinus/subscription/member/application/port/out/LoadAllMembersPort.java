package com.artinus.subscription.member.application.port.out;

import java.util.List;

import com.artinus.subscription.member.domain.Member;

public interface LoadAllMembersPort {

    List<Member> findAll();
}
