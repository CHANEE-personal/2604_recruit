package com.artinus.subscription.subscription.application.port.in;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

class ChangeSubscriptionCommandTest {

    @Test
    @DisplayName("모든 필드가 유효하면 예외가 발생하지 않는다")
    void validate_success() {
        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("01012345678")
                .channelId(1L)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        assertThatNoException().isThrownBy(command::validate);
    }


    @Test
    @DisplayName("phoneNumber가 null이면 예외가 발생한다")
    void validate_null_phone_number() {
        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber(null)
                .channelId(1L)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class);
    }


    @Test
    @DisplayName("phoneNumber가 공백이면 예외가 발생한다")
    void validate_blank_phone_number() {
        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("   ")
                .channelId(1L)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class);
    }


    @Test
    @DisplayName("channelId가 null이면 예외가 발생한다")
    void validate_null_channel_id() {
        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("01012345678")
                .channelId(null)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class);
    }


    @Test
    @DisplayName("targetStatus가 null이면 예외가 발생한다")
    void validate_null_target_status() {
        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("01012345678")
                .channelId(1L)
                .targetStatus(null)
                .build();

        assertThatThrownBy(command::validate).isInstanceOf(BusinessException.class);
    }
}
