package com.artinus.subscription.subscription.application.service;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.ErrorCode;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.application.port.out.DistributedLockPort;
import com.artinus.subscription.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChangeSubscriptionServiceTest {

    @InjectMocks
    private ChangeSubscriptionService changeSubscriptionService;

    @Mock
    private DistributedLockPort distributedLockPort;
    @Mock
    private ChangeSubscriptionExecutor executor;


    @Test
    @DisplayName("락을 획득하고 executor를 호출한다")
    void changeSubscription_acquires_lock_and_delegates() {
        willAnswer(invocation -> {
            ((Runnable) invocation.getArgument(1)).run();
            return null;
        }).given(distributedLockPort).executeWithLock(anyString(), any(Runnable.class));

        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("01012345678")
                .channelId(1L)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        changeSubscriptionService.changeSubscription(command);

        verify(distributedLockPort).executeWithLock(eq("subscription:lock:01012345678"), any());
        verify(executor).execute(command);
    }


    @Test
    @DisplayName("락 획득 실패 시 SUBSCRIPTION_LOCK_CONFLICT 예외가 전파된다")
    void changeSubscription_lock_conflict() {
        willThrow(new BusinessException(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT))
                .given(distributedLockPort).executeWithLock(anyString(), any(Runnable.class));

        ChangeSubscriptionCommand command = ChangeSubscriptionCommand.builder()
                .phoneNumber("01012345678")
                .channelId(1L)
                .targetStatus(SubscriptionStatus.BASIC)
                .build();

        assertThatThrownBy(() -> changeSubscriptionService.changeSubscription(command))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT));
    }
}
