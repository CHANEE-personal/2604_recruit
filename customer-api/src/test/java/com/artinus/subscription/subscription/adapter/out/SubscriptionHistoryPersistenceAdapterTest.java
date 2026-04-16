package com.artinus.subscription.subscription.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class SubscriptionHistoryPersistenceAdapterTest {

    @InjectMocks
    private SubscriptionHistoryPersistenceAdapter adapter;

    @Mock
    private SubscriptionHistoryJpaRepository subscriptionHistoryJpaRepository;
    @Mock
    private SubscriptionHistoryMapper subscriptionHistoryMapper;


    @Test
    @DisplayName("구독 이력을 저장한다")
    void save_success() {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .memberId(1L)
                .phoneNumber("01012345678")
                .channelId(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .build();
        SubscriptionHistoryJpaEntity entity = SubscriptionHistoryJpaEntity.builder()
                .memberId(1L)
                .phoneNumber("01012345678")
                .channelId(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .build();
        SubscriptionHistory saved = SubscriptionHistory.builder()
                .id(1L)
                .memberId(1L)
                .phoneNumber("01012345678")
                .channelId(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .build();

        given(subscriptionHistoryMapper.toEntity(history)).willReturn(entity);
        given(subscriptionHistoryJpaRepository.save(entity)).willReturn(entity);
        given(subscriptionHistoryMapper.toDomain(entity)).willReturn(saved);

        SubscriptionHistory result = adapter.save(history);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPhoneNumber()).isEqualTo("01012345678");
    }


    @Test
    @DisplayName("전화번호로 구독 이력을 최신순으로 조회한다")
    void findByPhoneNumber_returns_ordered_list() {
        SubscriptionHistoryJpaEntity e1 = SubscriptionHistoryJpaEntity.builder()
                .id(2L)
                .phoneNumber("01012345678")
                .memberId(1L)
                .channelId(1L)
                .channelName("채널A")
                .previousStatus(SubscriptionStatus.BASIC)
                .newStatus(SubscriptionStatus.PREMIUM)
                .build();
        SubscriptionHistoryJpaEntity e2 = SubscriptionHistoryJpaEntity.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .memberId(1L)
                .channelId(1L)
                .channelName("채널A")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .build();
        SubscriptionHistory h1 = SubscriptionHistory.builder()
                .id(2L)
                .build();
        SubscriptionHistory h2 = SubscriptionHistory.builder()
                .id(1L)
                .build();

        given(subscriptionHistoryJpaRepository.findByPhoneNumberOrderByCreatedAtDesc(
                "01012345678")).willReturn(List.of(e1, e2));
        given(subscriptionHistoryMapper.toDomain(e1)).willReturn(h1);
        given(subscriptionHistoryMapper.toDomain(e2)).willReturn(h2);

        List<SubscriptionHistory> result = adapter.findByPhoneNumber("01012345678");

        assertThat(result).hasSize(2);
        assertThat(result.get(0)
                .getId()).isEqualTo(2L);
        assertThat(result.get(1)
                .getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("이력이 없으면 빈 목록을 반환한다")
    void findByPhoneNumber_empty() {
        given(subscriptionHistoryJpaRepository.findByPhoneNumberOrderByCreatedAtDesc(
                "01099998888")).willReturn(List.of());

        List<SubscriptionHistory> result = adapter.findByPhoneNumber("01099998888");

        assertThat(result).isEmpty();
    }
}
