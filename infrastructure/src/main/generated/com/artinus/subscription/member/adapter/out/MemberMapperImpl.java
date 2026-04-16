package com.artinus.subscription.member.adapter.out;

import com.artinus.subscription.member.domain.Member;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T23:23:19+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
class MemberMapperImpl implements MemberMapper {

    @Override
    public MemberJpaEntity toEntity(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberJpaEntity.MemberJpaEntityBuilder memberJpaEntity = MemberJpaEntity.builder();

        memberJpaEntity.phoneNumber( member.getPhoneNumber() );
        memberJpaEntity.subscriptionStatus( member.getSubscriptionStatus() );

        return memberJpaEntity.build();
    }

    @Override
    public Member toDomain(MemberJpaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.id( entity.getId() );
        member.phoneNumber( entity.getPhoneNumber() );
        member.subscriptionStatus( entity.getSubscriptionStatus() );

        return member.build();
    }
}
