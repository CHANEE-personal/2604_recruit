package com.artinus.subscription.member.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.artinus.subscription.member.application.port.in.GetMemberQuery;
import com.artinus.subscription.member.application.port.out.LoadAllMembersPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class GetMemberService implements GetMemberQuery {

    private final LoadAllMembersPort loadAllMembersPort;


    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {
        return loadAllMembersPort.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
