package com.artinus.subscription.channel.application.port.out;

import java.util.List;

import com.artinus.subscription.channel.domain.Channel;

public interface LoadAllChannelsPort {

    List<Channel> findAll();
}
