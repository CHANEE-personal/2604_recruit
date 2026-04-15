package com.artinus.subscription.channel.domain;

import com.artinus.subscription.channel.domain.enums.ChannelType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Channel {

    private final Long id;
    private final String name;
    private final ChannelType channelType;


    public boolean canSubscribe() {
        return channelType.canSubscribe();
    }


    public boolean canUnsubscribe() {
        return channelType.canUnsubscribe();
    }
}
