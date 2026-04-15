package com.artinus.subscription.subscription.adapter.out;

import com.artinus.subscription.subscription.application.port.out.SummarizeHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class LlmApiAdapter implements SummarizeHistoryPort {

    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LlmFeignClient llmFeignClient;


    @CircuitBreaker(name = "llm", fallbackMethod = "fallbackSummarize")
    @Retry(name = "llm", fallbackMethod = "fallbackSummarize")
    @Override
    public String summarizeHistory(List<SubscriptionHistory> histories) {
        String historyText = buildHistoryText(histories);
        String prompt = "다음은 사용자의 구독 이력입니다. 이 이력을 한국어로 간략하게 요약해 주세요:\n\n" + historyText;

        LlmRequest request = LlmRequest.builder()
                .model(DEFAULT_MODEL)
                .messages(List.of(LlmRequest.Message.builder()
                        .role("system")
                        .content("당신은 구독 서비스 이력을 분석하고 요약하는 어시스턴트입니다.")
                        .build(), LlmRequest.Message.builder()
                        .role("user")
                        .content(prompt)
                        .build()))
                .maxTokens(500)
                .temperature(0.3)
                .build();

        try {
            LlmResponse response = llmFeignClient.chatCompletion(request);
            String content = response.getContent();
            if(content != null && !content.isBlank()) {
                return content;
            }
            log.warn("LLM returned empty response, falling back to default summary");
            return buildDefaultSummary(histories);
        } catch(Exception e) {
            log.error("LLM API call failed", e);
            return buildDefaultSummary(histories);
        }
    }


    public String fallbackSummarize(List<SubscriptionHistory> histories, Throwable throwable) {
        log.warn("LLM fallback triggered: {}. Returning default summary.", throwable.getMessage());
        return buildDefaultSummary(histories);
    }


    private String buildHistoryText(List<SubscriptionHistory> histories) {
        if(histories.isEmpty()) {
            return "구독 이력이 없습니다.";
        }
        return histories.stream()
                .map(h -> String.format("[%s] 채널: %s, %s → %s", h.getCreatedAt() != null ?
                        h.getCreatedAt()
                                .format(FORMATTER) :
                        "알 수 없음", h.getChannelName(), h.getPreviousStatus(), h.getNewStatus()))
                .collect(Collectors.joining("\n"));
    }


    private String buildDefaultSummary(List<SubscriptionHistory> histories) {
        if(histories.isEmpty()) {
            return "구독 이력이 없습니다.";
        }
        return String.format("총 %d건의 구독 이력이 있습니다. 최근 상태 변경: %s 채널에서 %s → %s", histories.size(),
                histories.get(0)
                        .getChannelName(), histories.get(0)
                        .getPreviousStatus(), histories.get(0)
                        .getNewStatus());
    }
}
