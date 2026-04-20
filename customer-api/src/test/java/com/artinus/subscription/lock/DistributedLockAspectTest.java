package com.artinus.subscription.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.common.lock.DistributedLock;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {

    private DistributedLockAspect aspect;

    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rLock;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature methodSignature;
    @Mock
    private DistributedLock distributedLock;


    @BeforeEach
    void setUp() {
        aspect = new DistributedLockAspect(redissonClient);
    }


    @Test
    @DisplayName("락 획득 성공 시 메소드가 실행되고 락이 해제된다")
    void around_lock_acquired_proceeds() throws Throwable {
        setupMocks("#command", new String[] {"command"},
                new Object[] {new TestCommand("01012345678")}, 5L, 3L);
        given(rLock.tryLock(5L, 3L, TimeUnit.SECONDS)).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(joinPoint.proceed()).willReturn("result");

        Object result = aspect.around(joinPoint, distributedLock);

        assertThat(result).isEqualTo("result");
        verify(joinPoint).proceed();
        verify(rLock).unlock();
    }


    @Test
    @DisplayName("락 획득 실패 시 SUBSCRIPTION_LOCK_CONFLICT 예외가 발생한다")
    void around_lock_not_acquired_throws() throws Throwable {
        setupMocks("#command", new String[] {"command"},
                new Object[] {new TestCommand("01012345678")}, 5L, 3L);
        given(rLock.tryLock(5L, 3L, TimeUnit.SECONDS)).willReturn(false);

        assertThatThrownBy(() -> aspect.around(joinPoint, distributedLock)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.SUBSCRIPTION_LOCK_CONFLICT));

        verify(joinPoint, never()).proceed();
        verify(rLock, never()).unlock();
    }


    @Test
    @DisplayName("InterruptedException 발생 시 SUBSCRIPTION_LOCK_CONFLICT 예외가 발생한다")
    void around_interrupted_throws() throws Throwable {
        setupMocks("#command", new String[] {"command"},
                new Object[] {new TestCommand("01012345678")}, 5L, 3L);
        given(rLock.tryLock(5L, 3L, TimeUnit.SECONDS)).willThrow(new InterruptedException());

        assertThatThrownBy(() -> aspect.around(joinPoint, distributedLock)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.SUBSCRIPTION_LOCK_CONFLICT));
    }


    @Test
    @DisplayName("락 키에 lock: 프리픽스가 붙는다")
    void around_lock_key_has_prefix() throws Throwable {
        setupMocks("#command", new String[] {"command"},
                new Object[] {new TestCommand("01012345678")}, 5L, 3L);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(joinPoint.proceed()).willReturn(null);

        aspect.around(joinPoint, distributedLock);

        verify(redissonClient).getLock("lock:01012345678");
    }


    @Test
    @DisplayName("메소드 실행 중 예외가 발생해도 락이 해제된다")
    void around_exception_in_proceed_still_releases_lock() throws Throwable {
        setupMocks("#command", new String[] {"command"},
                new Object[] {new TestCommand("01012345678")}, 5L, 3L);
        given(rLock.tryLock(5L, 3L, TimeUnit.SECONDS)).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(joinPoint.proceed()).willThrow(new RuntimeException("처리 오류"));

        assertThatThrownBy(() -> aspect.around(joinPoint, distributedLock)).isInstanceOf(
                RuntimeException.class);

        verify(rLock).unlock();
    }


    @Test
    @DisplayName("SpEL로 중첩 프로퍼티 키를 파싱한다")
    void around_spel_nested_property() throws Throwable {
        TestCommand command = new TestCommand("01099998888");
        setupMocks("#command.phoneNumber", new String[] {"command"}, new Object[] {command}, 5L,
                3L);
        given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
        given(rLock.isHeldByCurrentThread()).willReturn(true);
        given(joinPoint.proceed()).willReturn(null);

        aspect.around(joinPoint, distributedLock);

        verify(redissonClient).getLock("lock:01099998888");
    }


    private void setupMocks(String keyExpression, String[] paramNames, Object[] args, long waitTime,
            long leaseTime) {
        given(distributedLock.key()).willReturn(keyExpression);
        given(distributedLock.waitTime()).willReturn(waitTime);
        given(distributedLock.leaseTime()).willReturn(leaseTime);
        given(joinPoint.getSignature()).willReturn(methodSignature);
        given(methodSignature.getParameterNames()).willReturn(paramNames);
        given(joinPoint.getArgs()).willReturn(args);
        given(redissonClient.getLock(anyString())).willReturn(rLock);
    }


    static class TestCommand {

        private final String phoneNumber;


        TestCommand(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }


        public String getPhoneNumber() {
            return phoneNumber;
        }


        @Override
        public String toString() {
            return phoneNumber;
        }
    }
}
