package com.artinus.subscription.channel.application.port.out;

import com.artinus.subscription.channel.domain.Channel;

import java.util.List;

public interface LoadAllChannelsPort {

    List<Channel> findAll();
}
