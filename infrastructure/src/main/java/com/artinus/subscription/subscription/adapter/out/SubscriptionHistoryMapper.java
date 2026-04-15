package com.artinus.subscription.subscription.adapter.out;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
interface SubscriptionHistoryMapper {

    @Mapping(target = "id", ignore = true)
    SubscriptionHistoryJpaEntity toEntity(SubscriptionHistory history);

    SubscriptionHistory toDomain(SubscriptionHistoryJpaEntity entity);
}
