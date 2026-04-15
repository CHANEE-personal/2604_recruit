package com.artinus.subscription.channel.application.port.out;

import com.artinus.subscription.channel.domain.Channel;

public interface SaveChannelPort {

    Channel save(Channel channel);
}
