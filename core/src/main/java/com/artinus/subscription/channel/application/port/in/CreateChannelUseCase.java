package com.artinus.subscription.channel.application.port.in;

import com.artinus.subscription.channel.domain.Channel;

public interface CreateChannelUseCase {

    Channel createChannel(CreateChannelCommand command);
}
