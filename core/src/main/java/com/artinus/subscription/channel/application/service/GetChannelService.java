package com.artinus.subscription.channel.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.artinus.subscription.channel.application.port.in.GetChannelQuery;
import com.artinus.subscription.channel.application.port.out.LoadAllChannelsPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class GetChannelService implements GetChannelQuery {

    private final LoadAllChannelsPort loadAllChannelsPort;


    @Override
    @Transactional(readOnly = true)
    public List<ChannelResponse> getChannels() {
        return loadAllChannelsPort.findAll()
                .stream()
                .map(ChannelResponse::from)
                .toList();
    }
}
