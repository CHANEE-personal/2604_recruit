package com.artinus.subscription.csrng.adapter.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CsrngApiAdapterTest {

    private CsrngApiAdapter adapter;

    @Mock
    private CsrngFeignClient csrngFeignClient;


    @BeforeEach
    void setUp() {
        adapter = new CsrngApiAdapter(csrngFeignClient);
        ReflectionTestUtils.setField(adapter, "fallbackResult", true);
    }


    @Test
    @DisplayName("random=1 응답이면 true를 반환한다")
    void getRandomResult_random_one_returns_true() {
        CsrngResponse response = successResponse(1);
        given(csrngFeignClient.getRandom()).willReturn(List.of(response));

        assertThat(adapter.getRandomResult()).isTrue();
    }


    @Test
    @DisplayName("random=0 응답이면 false를 반환한다")
    void getRandomResult_random_zero_returns_false() {
        CsrngResponse response = successResponse(0);
        given(csrngFeignClient.getRandom()).willReturn(List.of(response));

        assertThat(adapter.getRandomResult()).isFalse();
    }


    @Test
    @DisplayName("빈 응답이면 EXTERNAL_API_ERROR 예외가 발생한다")
    void getRandomResult_empty_response_throws() {
        given(csrngFeignClient.getRandom()).willReturn(List.of());

        assertThatThrownBy(() -> adapter.getRandomResult()).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.EXTERNAL_API_ERROR));
    }


    @Test
    @DisplayName("null 응답이면 EXTERNAL_API_ERROR 예외가 발생한다")
    void getRandomResult_null_response_throws() {
        given(csrngFeignClient.getRandom()).willReturn(null);

        assertThatThrownBy(() -> adapter.getRandomResult()).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.EXTERNAL_API_ERROR));
    }


    @Test
    @DisplayName("status가 success가 아니면 EXTERNAL_API_ERROR 예외가 발생한다")
    void getRandomResult_non_success_status_throws() {
        CsrngResponse response = failResponse();
        given(csrngFeignClient.getRandom()).willReturn(List.of(response));

        assertThatThrownBy(() -> adapter.getRandomResult()).isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.EXTERNAL_API_ERROR));
    }


    @Test
    @DisplayName("fallback 호출 시 fallbackResult를 반환한다")
    void fallbackGetRandom_returns_fallback_result() {
        assertThat(adapter.fallbackGetRandom(new RuntimeException("오류"))).isTrue();
    }


    @Test
    @DisplayName("fallbackResult=false이면 fallback은 false를 반환한다")
    void fallbackGetRandom_false_fallback_result() {
        ReflectionTestUtils.setField(adapter, "fallbackResult", false);

        assertThat(adapter.fallbackGetRandom(new RuntimeException("오류"))).isFalse();
    }


    private CsrngResponse successResponse(int random) {
        CsrngResponse response = new CsrngResponse();
        ReflectionTestUtils.setField(response, "status", "success");
        ReflectionTestUtils.setField(response, "random", random);
        return response;
    }


    private CsrngResponse failResponse() {
        CsrngResponse response = new CsrngResponse();
        ReflectionTestUtils.setField(response, "status", "error");
        ReflectionTestUtils.setField(response, "random", 0);
        return response;
    }
}
