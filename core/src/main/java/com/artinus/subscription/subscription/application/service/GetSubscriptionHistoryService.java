package com.artinus.subscription.subscription.application.service;

import com.artinus.subscription.subscription.application.port.in.GetSubscriptionHistoryQuery;
import com.artinus.subscription.subscription.application.port.out.LoadSubscriptionHistoryPort;
import com.artinus.subscription.subscription.application.port.out.SummarizeHistoryPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
class GetSubscriptionHistoryService implements GetSubscriptionHistoryQuery {

    private final LoadSubscriptionHistoryPort loadSubscriptionHistoryPort;
    private final SummarizeHistoryPort summarizeHistoryPort;


    @Transactional(readOnly = true)
    @Override
    public HistoryResponse getHistory(String phoneNumber) {
        List<SubscriptionHistory> histories =
                loadSubscriptionHistoryPort.findByPhoneNumber(phoneNumber);

        List<HistoryItem> historyItems = histories.stream()
                .map(h -> HistoryItem.builder()
                        .id(h.getId())
                        .channelName(h.getChannelName())
                        .date(h.getCreatedAt())
                        .previousStatus(h.getPreviousStatus())
                        .subscriptionStatus(h.getNewStatus())
                        .build())
                .collect(Collectors.toList());

        return HistoryResponse.builder()
                .history(historyItems)
                .summary(summarizeHistoryPort.summarizeHistory(histories))
                .build();
    }
}
