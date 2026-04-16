package com.artinus.subscription.llm.adapter.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
class LlmResponse {

    private List<Choice> choices;


    public String getContent() {
        if(choices == null || choices.isEmpty()) {
            return null;
        }
        Message message = choices.get(0)
                .getMessage();
        return message != null ? message.getContent() : null;
    }


    @Getter
    @NoArgsConstructor
    static class Choice {

        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;
    }


    @Getter
    @NoArgsConstructor
    static class Message {

        private String role;
        private String content;
    }
}
