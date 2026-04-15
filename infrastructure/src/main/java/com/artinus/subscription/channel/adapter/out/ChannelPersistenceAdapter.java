package com.artinus.subscription.channel.adapter.out;

import com.artinus.subscription.channel.application.port.out.LoadAllChannelsPort;
import com.artinus.subscription.channel.application.port.out.LoadChannelPort;
import com.artinus.subscription.channel.application.port.out.SaveChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ChannelPersistenceAdapter implements LoadChannelPort, SaveChannelPort, LoadAllChannelsPort {

    private final ChannelJpaRepository channelJpaRepository;
    private final ChannelMapper channelMapper;


    public Optional<Channel> findById(Long channelId) {
        return channelJpaRepository.findById(channelId)
                .map(channelMapper::toDomain);
    }


    public Channel save(Channel channel) {
        ChannelJpaEntity entity = channelMapper.toEntity(channel);
        return channelMapper.toDomain(channelJpaRepository.save(entity));
    }


    public List<Channel> findAll() {
        return channelJpaRepository.findAll()
                .stream()
                .map(channelMapper::toDomain)
                .toList();
    }
}
