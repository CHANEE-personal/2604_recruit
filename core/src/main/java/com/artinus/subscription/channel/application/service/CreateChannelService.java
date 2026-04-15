package com.artinus.subscription.channel.application.service;

import com.artinus.subscription.channel.application.port.in.CreateChannelCommand;
import com.artinus.subscription.channel.application.port.in.CreateChannelUseCase;
import com.artinus.subscription.channel.application.port.out.SaveChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class CreateChannelService implements CreateChannelUseCase {

    private final SaveChannelPort saveChannelPort;


    @Override
    @Transactional
    public Channel createChannel(CreateChannelCommand command) {
        command.validate();
        Channel channel = Channel.builder()
                .name(command.getName())
                .channelType(command.getChannelType())
                .build();
        return saveChannelPort.save(channel);
    }
}
