package com.artinus.subscription.channel.application.port.in;

import com.artinus.subscription.channel.application.port.in.GetChannelQuery.ChannelResponse;

public interface CreateChannelUseCase {

    ChannelResponse createChannel(CreateChannelCommand command);
}
