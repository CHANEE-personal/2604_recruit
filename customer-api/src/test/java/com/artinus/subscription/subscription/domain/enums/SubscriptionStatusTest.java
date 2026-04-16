package com.artinus.subscription.subscription.domain.enums;

import static com.artinus.subscription.subscription.domain.enums.SubscriptionStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SubscriptionStatusTest {

    @Test
    @DisplayName("NONE은 BASIC으로 구독할 수 있다")
    void canSubscribeTo_none_to_basic() {
        assertThat(NONE.canSubscribeTo(BASIC)).isTrue();
    }


    @Test
    @DisplayName("NONE은 PREMIUM으로 구독할 수 있다")
    void canSubscribeTo_none_to_premium() {
        assertThat(NONE.canSubscribeTo(PREMIUM)).isTrue();
    }


    @Test
    @DisplayName("BASIC은 PREMIUM으로 업그레이드할 수 있다")
    void canSubscribeTo_basic_to_premium() {
        assertThat(BASIC.canSubscribeTo(PREMIUM)).isTrue();
    }


    @Test
    @DisplayName("NONE은 NONE으로 구독할 수 없다")
    void canSubscribeTo_none_to_none() {
        assertThat(NONE.canSubscribeTo(NONE)).isFalse();
    }


    @Test
    @DisplayName("BASIC은 NONE으로 구독할 수 없다")
    void canSubscribeTo_basic_to_none() {
        assertThat(BASIC.canSubscribeTo(NONE)).isFalse();
    }


    @Test
    @DisplayName("BASIC은 BASIC으로 구독할 수 없다")
    void canSubscribeTo_basic_to_basic() {
        assertThat(BASIC.canSubscribeTo(BASIC)).isFalse();
    }


    @Test
    @DisplayName("PREMIUM은 어느 상태로도 구독할 수 없다")
    void canSubscribeTo_premium_to_any() {
        assertThat(PREMIUM.canSubscribeTo(NONE)).isFalse();
        assertThat(PREMIUM.canSubscribeTo(BASIC)).isFalse();
        assertThat(PREMIUM.canSubscribeTo(PREMIUM)).isFalse();
    }


    @Test
    @DisplayName("PREMIUM은 BASIC으로 다운그레이드할 수 있다")
    void canUnsubscribeTo_premium_to_basic() {
        assertThat(PREMIUM.canUnsubscribeTo(BASIC)).isTrue();
    }


    @Test
    @DisplayName("PREMIUM은 NONE으로 완전 해지할 수 있다")
    void canUnsubscribeTo_premium_to_none() {
        assertThat(PREMIUM.canUnsubscribeTo(NONE)).isTrue();
    }


    @Test
    @DisplayName("BASIC은 NONE으로 해지할 수 있다")
    void canUnsubscribeTo_basic_to_none() {
        assertThat(BASIC.canUnsubscribeTo(NONE)).isTrue();
    }


    @Test
    @DisplayName("BASIC은 PREMIUM으로 해지할 수 없다")
    void canUnsubscribeTo_basic_to_premium() {
        assertThat(BASIC.canUnsubscribeTo(PREMIUM)).isFalse();
    }


    @Test
    @DisplayName("BASIC은 BASIC으로 해지할 수 없다")
    void canUnsubscribeTo_basic_to_basic() {
        assertThat(BASIC.canUnsubscribeTo(BASIC)).isFalse();
    }


    @Test
    @DisplayName("NONE은 어느 상태로도 해지할 수 없다")
    void canUnsubscribeTo_none_to_any() {
        assertThat(NONE.canUnsubscribeTo(NONE)).isFalse();
        assertThat(NONE.canUnsubscribeTo(BASIC)).isFalse();
        assertThat(NONE.canUnsubscribeTo(PREMIUM)).isFalse();
    }


    @Test
    @DisplayName("PREMIUM은 PREMIUM으로 해지할 수 없다")
    void canUnsubscribeTo_premium_to_premium() {
        assertThat(PREMIUM.canUnsubscribeTo(PREMIUM)).isFalse();
    }


    @Test
    @DisplayName("NONE에서 BASIC은 업그레이드다")
    void isUpgradeTo_none_to_basic() {
        assertThat(NONE.isUpgradeTo(BASIC)).isTrue();
    }


    @Test
    @DisplayName("NONE에서 PREMIUM은 업그레이드다")
    void isUpgradeTo_none_to_premium() {
        assertThat(NONE.isUpgradeTo(PREMIUM)).isTrue();
    }


    @Test
    @DisplayName("BASIC에서 PREMIUM은 업그레이드다")
    void isUpgradeTo_basic_to_premium() {
        assertThat(BASIC.isUpgradeTo(PREMIUM)).isTrue();
    }


    @Test
    @DisplayName("BASIC에서 NONE은 업그레이드가 아니다")
    void isUpgradeTo_basic_to_none() {
        assertThat(BASIC.isUpgradeTo(NONE)).isFalse();
    }


    @Test
    @DisplayName("PREMIUM에서 BASIC은 업그레이드가 아니다")
    void isUpgradeTo_premium_to_basic() {
        assertThat(PREMIUM.isUpgradeTo(BASIC)).isFalse();
    }


    @Test
    @DisplayName("동일 상태는 업그레이드가 아니다")
    void isUpgradeTo_same_status() {
        assertThat(NONE.isUpgradeTo(NONE)).isFalse();
        assertThat(BASIC.isUpgradeTo(BASIC)).isFalse();
        assertThat(PREMIUM.isUpgradeTo(PREMIUM)).isFalse();
    }
}
