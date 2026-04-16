package com.artinus.subscription.channel.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.artinus.subscription.channel.domain.enums.ChannelType;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;

class CreateChannelCommandTest {

    @Test
    @DisplayName("모든 필드가 유효하면 예외가 발생하지 않는다")
    void validate_success() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();

        assertThatNoException().isThrownBy(command::validate);
    }


    @Test
    @DisplayName("name이 null이면 INVALID_CHANNEL_NAME 예외가 발생한다")
    void validate_null_name() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name(null)
                .channelType(ChannelType.BOTH)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_NAME));
    }


    @Test
    @DisplayName("name이 공백이면 INVALID_CHANNEL_NAME 예외가 발생한다")
    void validate_blank_name() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("   ")
                .channelType(ChannelType.BOTH)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_NAME));
    }


    @Test
    @DisplayName("channelType이 null이면 INVALID_CHANNEL_TYPE 예외가 발생한다")
    void validate_null_channel_type() {
        CreateChannelCommand command = CreateChannelCommand.builder()
                .name("기본 채널")
                .channelType(null)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_CHANNEL_TYPE));
    }
}
