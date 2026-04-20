package com.artinus.subscription.channel.application.port.out;

import java.util.Optional;

import com.artinus.subscription.channel.domain.Channel;

public interface LoadChannelPort {

    Optional<Channel> findById(Long channelId);

    boolean existsByName(String name);
}
