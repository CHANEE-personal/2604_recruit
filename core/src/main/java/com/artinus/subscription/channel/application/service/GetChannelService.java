package com.artinus.subscription.channel.application.service;

import com.artinus.subscription.channel.application.port.in.GetChannelQuery;
import com.artinus.subscription.channel.application.port.out.LoadAllChannelsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
