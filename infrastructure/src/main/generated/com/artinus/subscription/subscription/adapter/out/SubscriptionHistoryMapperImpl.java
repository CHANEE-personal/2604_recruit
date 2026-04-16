package com.artinus.subscription.subscription.adapter.out;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T23:23:19+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
class SubscriptionHistoryMapperImpl implements SubscriptionHistoryMapper {

    @Override
    public SubscriptionHistoryJpaEntity toEntity(SubscriptionHistory history) {
        if ( history == null ) {
            return null;
        }

        SubscriptionHistoryJpaEntity.SubscriptionHistoryJpaEntityBuilder subscriptionHistoryJpaEntity = SubscriptionHistoryJpaEntity.builder();

        subscriptionHistoryJpaEntity.memberId( history.getMemberId() );
        subscriptionHistoryJpaEntity.phoneNumber( history.getPhoneNumber() );
        subscriptionHistoryJpaEntity.channelId( history.getChannelId() );
        subscriptionHistoryJpaEntity.channelName( history.getChannelName() );
        subscriptionHistoryJpaEntity.previousStatus( history.getPreviousStatus() );
        subscriptionHistoryJpaEntity.newStatus( history.getNewStatus() );
        subscriptionHistoryJpaEntity.createdAt( history.getCreatedAt() );

        return subscriptionHistoryJpaEntity.build();
    }

    @Override
    public SubscriptionHistory toDomain(SubscriptionHistoryJpaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        SubscriptionHistory.SubscriptionHistoryBuilder subscriptionHistory = SubscriptionHistory.builder();

        subscriptionHistory.id( entity.getId() );
        subscriptionHistory.memberId( entity.getMemberId() );
        subscriptionHistory.phoneNumber( entity.getPhoneNumber() );
        subscriptionHistory.channelId( entity.getChannelId() );
        subscriptionHistory.channelName( entity.getChannelName() );
        subscriptionHistory.previousStatus( entity.getPreviousStatus() );
        subscriptionHistory.newStatus( entity.getNewStatus() );
        subscriptionHistory.createdAt( entity.getCreatedAt() );

        return subscriptionHistory.build();
    }
}
