package com.artinus.subscription.channel.domain.enums;

import static com.artinus.subscription.channel.domain.enums.ChannelType.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChannelTypeTest {

    @Test
    @DisplayName("BOTH 채널은 구독이 가능하다")
    void both_canSubscribe() {
        assertThat(BOTH.canSubscribe()).isTrue();
    }


    @Test
    @DisplayName("BOTH 채널은 해지가 가능하다")
    void both_canUnsubscribe() {
        assertThat(BOTH.canUnsubscribe()).isTrue();
    }


    @Test
    @DisplayName("SUBSCRIBE_ONLY 채널은 구독이 가능하다")
    void subscribeOnly_canSubscribe() {
        assertThat(SUBSCRIBE_ONLY.canSubscribe()).isTrue();
    }


    @Test
    @DisplayName("SUBSCRIBE_ONLY 채널은 해지가 불가능하다")
    void subscribeOnly_canNotUnsubscribe() {
        assertThat(SUBSCRIBE_ONLY.canUnsubscribe()).isFalse();
    }


    @Test
    @DisplayName("UNSUBSCRIBE_ONLY 채널은 구독이 불가능하다")
    void unsubscribeOnly_canNotSubscribe() {
        assertThat(UNSUBSCRIBE_ONLY.canSubscribe()).isFalse();
    }


    @Test
    @DisplayName("UNSUBSCRIBE_ONLY 채널은 해지가 가능하다")
    void unsubscribeOnly_canUnsubscribe() {
        assertThat(UNSUBSCRIBE_ONLY.canUnsubscribe()).isTrue();
    }
}
