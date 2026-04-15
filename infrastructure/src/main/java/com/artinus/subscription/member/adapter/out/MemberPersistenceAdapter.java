package com.artinus.subscription.member.adapter.out;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.ErrorCode;
import com.artinus.subscription.member.application.port.out.LoadAllMembersPort;
import com.artinus.subscription.member.application.port.out.LoadMemberPort;
import com.artinus.subscription.member.application.port.out.SaveMemberPort;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class MemberPersistenceAdapter implements LoadMemberPort, SaveMemberPort, LoadAllMembersPort {

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


    public Member updateStatus(String phoneNumber, SubscriptionStatus newStatus) {
        MemberJpaEntity entity = memberJpaRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        entity.updateStatus(newStatus);
        return memberMapper.toDomain(memberJpaRepository.save(entity));
    }


    public List<Member> findAll() {
        return memberJpaRepository.findAll()
                .stream()
                .map(memberMapper::toDomain)
                .toList();
    }
}
