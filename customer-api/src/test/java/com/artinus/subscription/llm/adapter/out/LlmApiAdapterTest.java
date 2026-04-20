package com.artinus.subscription.llm.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class LlmApiAdapterTest {

    private LlmApiAdapter adapter;

    @Mock
    private LlmFeignClient llmFeignClient;


    @BeforeEach
    void setUp() {
        adapter = new LlmApiAdapter(llmFeignClient);
    }


    @Test
    @DisplayName("LLM API가 정상 응답하면 요약 내용을 반환한다")
    void summarizeHistory_success() {
        LlmResponse response = responseWithContent("총 1건의 구독 이력이 있습니다.");
        given(llmFeignClient.chatCompletion(any())).willReturn(response);

        String result = adapter.summarizeHistory(List.of(history()));

        assertThat(result).isEqualTo("총 1건의 구독 이력이 있습니다.");
    }


    @Test
    @DisplayName("LLM API가 빈 content를 반환하면 기본 요약을 반환한다")
    void summarizeHistory_empty_content_returns_default() {
        LlmResponse response = responseWithContent("");
        given(llmFeignClient.chatCompletion(any())).willReturn(response);

        String result = adapter.summarizeHistory(List.of(history()));

        assertThat(result).contains("총 1건의 구독 이력이 있습니다");
    }


    @Test
    @DisplayName("LLM API 호출 중 예외가 발생하면 fallback이 기본 요약을 반환한다")
    void summarizeHistory_exception_returns_default() {
        String result =
                adapter.fallbackSummarize(List.of(history()), new RuntimeException("API 오류"));

        assertThat(result).contains("총 1건의 구독 이력이 있습니다");
    }


    @Test
    @DisplayName("이력이 없으면 '구독 이력이 없습니다.'를 반환한다")
    void summarizeHistory_empty_histories() {
        String result = adapter.summarizeHistory(List.of());

        assertThat(result).isEqualTo("구독 이력이 없습니다.");
    }


    @Test
    @DisplayName("createdAt이 null이면 기본 요약에서 '알 수 없음'으로 표시된다")
    void summarizeHistory_null_createdAt() {
        SubscriptionHistory history = SubscriptionHistory.builder()
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .createdAt(null)
                .build();

        String result = adapter.fallbackSummarize(List.of(history), new RuntimeException("오류"));

        assertThat(result).contains("총 1건의 구독 이력이 있습니다");
    }


    @Test
    @DisplayName("fallback 호출 시 기본 요약을 반환한다")
    void fallbackSummarize_returns_default_summary() {
        List<SubscriptionHistory> histories = List.of(history());

        String result = adapter.fallbackSummarize(histories, new RuntimeException("Circuit Open"));

        assertThat(result).contains("총 1건의 구독 이력이 있습니다");
    }


    @Test
    @DisplayName("fallback에서 이력이 없으면 '구독 이력이 없습니다.'를 반환한다")
    void fallbackSummarize_empty_histories() {
        String result = adapter.fallbackSummarize(List.of(), new RuntimeException("Circuit Open"));

        assertThat(result).isEqualTo("구독 이력이 없습니다.");
    }


    private SubscriptionHistory history() {
        return SubscriptionHistory.builder()
                .id(1L)
                .channelName("기본 채널")
                .previousStatus(SubscriptionStatus.NONE)
                .newStatus(SubscriptionStatus.BASIC)
                .createdAt(LocalDateTime.now())
                .build();
    }


    private LlmResponse responseWithContent(String content) {
        LlmResponse response = new LlmResponse();
        if(content != null) {
            LlmResponse.Message message = new LlmResponse.Message();
            ReflectionTestUtils.setField(message, "content", content);
            LlmResponse.Choice choice = new LlmResponse.Choice();
            ReflectionTestUtils.setField(choice, "message", message);
            ReflectionTestUtils.setField(response, "choices", List.of(choice));
        }
        return response;
    }
}
