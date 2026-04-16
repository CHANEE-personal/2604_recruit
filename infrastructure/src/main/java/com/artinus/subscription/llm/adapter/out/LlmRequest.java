package com.artinus.subscription.llm.adapter.out;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class LlmRequest {

    private final String model;
    private final List<Message> messages;

    @JsonProperty("max_tokens")
    private final int maxTokens;

    private final double temperature;


    @Getter
    @Builder
    static class Message {

        private final String role;
        private final String content;
    }
}
