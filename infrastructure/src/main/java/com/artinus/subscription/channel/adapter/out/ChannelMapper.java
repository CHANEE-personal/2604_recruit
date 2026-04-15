package com.artinus.subscription.channel.adapter.out;

import com.artinus.subscription.channel.domain.Channel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface ChannelMapper {

    @Mapping(target = "id", ignore = true)
    ChannelJpaEntity toEntity(Channel channel);

    Channel toDomain(ChannelJpaEntity entity);
}
