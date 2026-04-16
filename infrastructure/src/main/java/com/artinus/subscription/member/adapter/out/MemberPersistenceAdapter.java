package com.artinus.subscription.member.adapter.out;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.artinus.subscription.member.application.port.out.LoadAllMembersPort;
import com.artinus.subscription.member.application.port.out.LoadMemberPort;
import com.artinus.subscription.member.application.port.out.SaveMemberPort;
import com.artinus.subscription.member.application.port.out.UpdateMemberStatusPort;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class MemberPersistenceAdapter
        implements LoadMemberPort, SaveMemberPort, UpdateMemberStatusPort, LoadAllMembersPort {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberMapper memberMapper;


    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return memberJpaRepository.findByPhoneNumber(phoneNumber)
                .map(memberMapper::toDomain);
    }


    public Member save(Member member) {
        MemberJpaEntity entity = memberMapper.toEntity(member);
        return memberMapper.toDomain(memberJpaRepository.save(entity));
    }


    public void updateStatus(String phoneNumber, SubscriptionStatus newStatus) {
        memberJpaRepository.updateSubscriptionStatus(phoneNumber, newStatus);
    }


    public List<Member> findAll() {
        return memberJpaRepository.findAll()
                .stream()
                .map(memberMapper::toDomain)
                .toList();
    }
}
