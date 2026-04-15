package com.artinus.subscription.member.adapter.out;

import com.artinus.subscription.member.domain.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    MemberJpaEntity toEntity(Member member);

    Member toDomain(MemberJpaEntity entity);
}
