package com.artinus.subscription.csrng.adapter.out;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.subscription.application.port.out.GetRandomResultPort;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
class CsrngApiAdapter implements GetRandomResultPort {

    private final CsrngFeignClient csrngFeignClient;

    @Value("${csrng.fallback-result:true}")
    private boolean fallbackResult;


    @Override
    @CircuitBreaker(name = "csrng", fallbackMethod = "fallbackGetRandom")
    @Retry(name = "csrng")
    public boolean getRandomResult() {
        List<CsrngResponse> responses = csrngFeignClient.getRandom();
        if(responses == null || responses.isEmpty()) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "empty response");
        }
        CsrngResponse result = responses.get(0);
        if(!result.isSuccess()) {
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "status=" + result.getStatus());
        }
        log.info("csrng response: status={}, random={}", result.getStatus(), result.getRandom());
        return result.isRandomOne();
    }


    public boolean fallbackGetRandom(Throwable throwable) {
        log.warn("csrng fallback triggered: {}. Defaulting to fallback-result={}",
                throwable.getMessage(), fallbackResult);
        return fallbackResult;
    }
}
