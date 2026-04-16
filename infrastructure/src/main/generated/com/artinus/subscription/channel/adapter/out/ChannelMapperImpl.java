package com.artinus.subscription.channel.adapter.out;

import com.artinus.subscription.channel.domain.Channel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-15T23:23:19+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
class ChannelMapperImpl implements ChannelMapper {

    @Override
    public ChannelJpaEntity toEntity(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        ChannelJpaEntity.ChannelJpaEntityBuilder channelJpaEntity = ChannelJpaEntity.builder();

        channelJpaEntity.name( channel.getName() );
        channelJpaEntity.channelType( channel.getChannelType() );

        return channelJpaEntity.build();
    }

    @Override
    public Channel toDomain(ChannelJpaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Channel.ChannelBuilder channel = Channel.builder();

        channel.id( entity.getId() );
        channel.name( entity.getName() );
        channel.channelType( entity.getChannelType() );

        return channel.build();
    }
}
