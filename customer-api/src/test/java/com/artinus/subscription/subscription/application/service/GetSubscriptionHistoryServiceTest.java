package com.artinus.subscription.subscription.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.subscription.application.port.in.GetSubscriptionHistoryQuery.HistoryItem;
import com.artinus.subscription.subscription.application.port.in.GetSubscriptionHistoryQuery.HistoryResponse;
import com.artinus.subscription.subscription.application.port.out.LoadSubscriptionHistoryPort;
import com.artinus.subscription.subscription.application.port.out.SummarizeHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class GetSubscriptionHistoryServiceTest {

    @InjectMocks
    private GetSubscriptionHistoryService service;

    @Mock
    private LoadSubscriptionHistoryPort loadSubscriptionHistoryPort;
    @Mock
    private SummarizeHistoryPort summarizeHistoryPort;


    @Test
    @DisplayName("전화번호로 이력을 조회하면 HistoryItem 목록과 요약을 반환한다")
    void getHistory_success() {
        LocalDateTime now = LocalDateTime.now();
        SubscriptionHistory history = SubscriptionHistory.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .channelId(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .createdAt(now)
                .build();

        given(loadSubscriptionHistoryPort.findByPhoneNumber("01012345678")).willReturn(
                List.of(history));
        given(summarizeHistoryPort.summarizeHistory(List.of(history))).willReturn(
                "총 1건의 구독 이력이 있습니다.");

        HistoryResponse response = service.getHistory("01012345678");

        assertThat(response.getHistory()).hasSize(1);
        HistoryItem item = response.getHistory()
                .get(0);
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getChannelName()).isEqualTo("기본 채널");
        assertThat(item.getDate()).isEqualTo(now);
        assertThat(item.getPreviousStatus()).isEqualTo(SubscriptionStatus.NONE);
        assertThat(item.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.BASIC);
        assertThat(response.getSummary()).isEqualTo("총 1건의 구독 이력이 있습니다.");
    }


    @Test
    @DisplayName("이력이 없으면 빈 목록과 요약을 반환한다")
    void getHistory_empty() {
        given(loadSubscriptionHistoryPort.findByPhoneNumber("01099998888")).willReturn(List.of());

        HistoryResponse response = service.getHistory("01099998888");

        assertThat(response.getHistory()).isEmpty();
        assertThat(response.getSummary()).isEqualTo("구독 이력이 없습니다.");
    }


    @Test
    @DisplayName("여러 이력이 있으면 전부 변환하여 반환한다")
    void getHistory_multiple() {
        SubscriptionHistory h1 = SubscriptionHistory.builder()
                .id(1L)
                .channelName("채널A")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .createdAt(LocalDateTime.now())
                .build();
        SubscriptionHistory h2 = SubscriptionHistory.builder()
                .id(2L)
                .channelName("채널B")
                .previousStatus(SubscriptionStatus.BASIC)
                .newStatus(SubscriptionStatus.PREMIUM)
                .createdAt(LocalDateTime.now())
                .build();

        given(loadSubscriptionHistoryPort.findByPhoneNumber("01012345678")).willReturn(
                List.of(h1, h2));
        given(summarizeHistoryPort.summarizeHistory(List.of(h1, h2))).willReturn("총 2건");

        HistoryResponse response = service.getHistory("01012345678");

        assertThat(response.getHistory()).hasSize(2);
        verify(summarizeHistoryPort).summarizeHistory(List.of(h1, h2));
    }


    @Test
    @DisplayName("createdAt이 null이어도 정상적으로 변환된다")
    void getHistory_null_createdAt() {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .id(1L)
                .channelName("채널A")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .createdAt(null)
                .build();

        given(loadSubscriptionHistoryPort.findByPhoneNumber("01012345678")).willReturn(
                List.of(history));
        given(summarizeHistoryPort.summarizeHistory(List.of(history))).willReturn("요약");

        HistoryResponse response = service.getHistory("01012345678");

        assertThat(response.getHistory()
                .get(0)
                .getDate()).isNull();
    }
}
