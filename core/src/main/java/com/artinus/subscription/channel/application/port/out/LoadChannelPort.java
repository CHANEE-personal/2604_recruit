package com.artinus.subscription.channel.application.port.out;

import com.artinus.subscription.channel.domain.Channel;

import java.util.Optional;

public interface LoadChannelPort {

    Optional<Channel> findById(Long channelId);
}
