package com.artinus.subscription.llm.adapter.out;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "llm", url = "${openai.base-url}", configuration = LlmFeignConfig.class)
interface LlmFeignClient {

    @PostMapping("/v1/chat/completions")
    LlmResponse chatCompletion(@RequestBody LlmRequest request);
}
