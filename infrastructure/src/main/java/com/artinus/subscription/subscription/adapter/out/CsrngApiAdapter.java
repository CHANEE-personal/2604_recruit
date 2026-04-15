package com.artinus.subscription.subscription.adapter.out;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.subscription.application.port.out.GetRandomResultPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class CsrngApiAdapter implements GetRandomResultPort {

    private final CsrngFeignClient csrngFeignClient;


    @Override
    @CircuitBreaker(name = "csrng", fallbackMethod = "fallbackGetRandom")
    @Retry(name = "csrng", fallbackMethod = "fallbackGetRandom")
    public boolean getRandomResult() {
        try {
            List<CsrngResponse> responses = csrngFeignClient.getRandom();
            if(responses != null && !responses.isEmpty()) {
                CsrngResponse result = responses.get(0);
                log.info("csrng response: status={}, random={}", result.getStatus(),
                        result.getRandom());
                return result.isRandomOne();
            }
            log.warn("csrng returned empty response, defaulting to true");
            return true;
        } catch(Exception e) {
            log.error("csrng API call failed", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, e.getMessage());
        }
    }


    public boolean fallbackGetRandom(Throwable throwable) {
        log.warn("csrng fallback triggered: {}. Defaulting to random=1 (commit)",
                throwable.getMessage());
        return true;
    }
}
