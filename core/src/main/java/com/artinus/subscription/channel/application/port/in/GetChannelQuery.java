package com.artinus.subscription.channel.application.port.in;

import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.channel.domain.ChannelType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public interface GetChannelQuery {

    List<ChannelResponse> getChannels();


    @Getter
    @Builder
    class ChannelResponse {

        private final Long id;
        private final String name;
        private final ChannelType channelType;


        public static ChannelResponse from(Channel channel) {
            return ChannelResponse.builder()
                    .id(channel.getId())
                    .name(channel.getName())
                    .channelType(channel.getChannelType())
                    .build();
        }
    }
}
