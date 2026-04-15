package com.artinus.subscription.subscription.adapter.out;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

class LlmFeignConfig {

    @Value("${openai.api-key}")
    private String apiKey;


    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return template -> template.header("Authorization", "Bearer " + apiKey);
    }
}
