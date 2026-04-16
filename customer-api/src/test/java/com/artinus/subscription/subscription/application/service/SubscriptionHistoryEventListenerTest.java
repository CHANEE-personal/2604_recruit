package com.artinus.subscription.subscription.application.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.subscription.application.port.out.SaveSubscriptionHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.SubscriptionHistoryEvent;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class SubscriptionHistoryEventListenerTest {

    @InjectMocks
    private SubscriptionHistoryEventListener listener;

    @Mock
    private SaveSubscriptionHistoryPort saveSubscriptionHistoryPort;


    @Test
    @DisplayName("이벤트 수신 시 구독 이력을 저장한다")
    void handle_success() {
        SubscriptionHistory history = history();
        SubscriptionHistoryEvent event = new SubscriptionHistoryEvent(history);

        listener.handle(event);

        verify(saveSubscriptionHistoryPort).save(history);
    }


    @Test
    @DisplayName("저장 중 예외가 발생해도 외부로 전파되지 않는다")
    void handle_save_exception_swallowed() {
        SubscriptionHistoryEvent event = new SubscriptionHistoryEvent(history());
        doThrow(new RuntimeException("DB 오류")).when(saveSubscriptionHistoryPort)
                .save(any());

        assertThatNoException().isThrownBy(() -> listener.handle(event));
    }


    private SubscriptionHistory history() {
        return SubscriptionHistory.builder()
                .id(1L)
                .memberId(1L)
                .phoneNumber("01012345678")
                .channelId(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .build();
    }
}
