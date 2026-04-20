package com.artinus.subscription.channel.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.channel.application.port.in.CreateChannelCommand;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery.ChannelResponse;
import com.artinus.subscription.channel.application.port.out.LoadChannelPort;
import com.artinus.subscription.channel.application.port.out.SaveChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.channel.domain.enums.ChannelType;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CreateChannelServiceTest {

    @InjectMocks
    private CreateChannelService service;

    @Mock
    private SaveChannelPort saveChannelPort;

    @Mock
    private LoadChannelPort loadChannelPort;


    @Test
    @DisplayName("유효한 커맨드로 채널을 생성한다")
    void createChannel_success() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();
        Channel saved = Channel.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();
        given(loadChannelPort.existsByName("기본 채널")).willReturn(false);
        given(saveChannelPort.save(any())).willReturn(saved);

        ChannelResponse result = service.createChannel(command);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("기본 채널");
        assertThat(result.getChannelType()).isEqualTo(ChannelType.BOTH);
        verify(saveChannelPort).save(any());
    }


    @Test
    @DisplayName("채널 이름이 null이면 예외가 발생한다")
    void createChannel_null_name() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name(null)
                .channelType(ChannelType.BOTH)
                .build();

        assertThatThrownBy(() -> service.createChannel(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_NAME));
    }


    @Test
    @DisplayName("채널 이름이 공백이면 예외가 발생한다")
    void createChannel_blank_name() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("   ")
                .channelType(ChannelType.BOTH)
                .build();

        assertThatThrownBy(() -> service.createChannel(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_NAME));
    }


    @Test
    @DisplayName("채널 타입이 null이면 예외가 발생한다")
    void createChannel_null_channelType() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("기본 채널")
                .channelType(null)
                .build();

        assertThatThrownBy(() -> service.createChannel(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_TYPE));
    }
}
