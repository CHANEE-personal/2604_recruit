package com.artinus.subscription.channel.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.channel.domain.enums.ChannelType;

@ExtendWith(MockitoExtension.class)
class ChannelPersistenceAdapterTest {

    @InjectMocks
    private ChannelPersistenceAdapter adapter;

    @Mock
    private ChannelJpaRepository channelJpaRepository;
    @Mock
    private ChannelMapper channelMapper;


    @Test
    @DisplayName("ID로 채널을 조회한다")
    void findById_found() {
        ChannelJpaEntity entity = ChannelJpaEntity.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();
        Channel channel = Channel.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();

        given(channelJpaRepository.findById(1L)).willReturn(Optional.of(entity));
        given(channelMapper.toDomain(entity)).willReturn(channel);

        Optional<Channel> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get()
                .getName()).isEqualTo("기본 채널");
    }


    @Test
    @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
    void findById_not_found() {
        given(channelJpaRepository.findById(99L)).willReturn(Optional.empty());

        Optional<Channel> result = adapter.findById(99L);

        assertThat(result).isEmpty();
    }


    @Test
    @DisplayName("채널을 저장한다")
    void save_success() {
        Channel channel = Channel.builder()
                .name("신규 채널")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build();
        ChannelJpaEntity entity = ChannelJpaEntity.builder()
                .name("신규 채널")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build();
        Channel saved = Channel.builder()
                .id(1L)
                .name("신규 채널")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build();

        given(channelMapper.toEntity(channel)).willReturn(entity);
        given(channelJpaRepository.save(entity)).willReturn(entity);
        given(channelMapper.toDomain(entity)).willReturn(saved);

        Channel result = adapter.save(channel);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("신규 채널");
    }


    @Test
    @DisplayName("전체 채널 목록을 조회한다")
    void findAll_success() {
        ChannelJpaEntity e1 = ChannelJpaEntity.builder()
                .id(1L)
                .name("채널A")
                .channelType(ChannelType.BOTH)
                .build();
        ChannelJpaEntity e2 = ChannelJpaEntity.builder()
                .id(2L)
                .name("채널B")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build();
        Channel c1 = Channel.builder()
                .id(1L)
                .build();
        Channel c2 = Channel.builder()
                .id(2L)
                .build();

        given(channelJpaRepository.findAll()).willReturn(List.of(e1, e2));
        given(channelMapper.toDomain(e1)).willReturn(c1);
        given(channelMapper.toDomain(e2)).willReturn(c2);

        List<Channel> result = adapter.findAll();

        assertThat(result).hasSize(2);
    }


    @Test
    @DisplayName("빈 목록을 반환한다")
    void findAll_empty() {
        given(channelJpaRepository.findAll()).willReturn(List.of());

        List<Channel> result = adapter.findAll();

        assertThat(result).isEmpty();
    }
}
