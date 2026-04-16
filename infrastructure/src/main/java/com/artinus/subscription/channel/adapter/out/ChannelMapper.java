package com.artinus.subscription.channel.adapter.out;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.artinus.subscription.channel.domain.Channel;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ChannelMapper {

    @Mapping(target = "id", ignore = true)
    ChannelJpaEntity toEntity(Channel channel);

    Channel toDomain(ChannelJpaEntity entity);
}
