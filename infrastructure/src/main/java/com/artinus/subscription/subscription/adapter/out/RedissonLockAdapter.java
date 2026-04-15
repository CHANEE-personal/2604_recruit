package com.artinus.subscription.subscription.adapter.out;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.ErrorCode;
import com.artinus.subscription.subscription.application.port.out.DistributedLockPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
class RedissonLockAdapter implements DistributedLockPort {

    private static final long WAIT_TIME_SECONDS = 5;
    private static final long LEASE_TIME_SECONDS = 3;

    private final RedissonClient redissonClient;


    @Override
    public void executeWithLock(String lockKey, Runnable task) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);
            if(!acquired) {
                log.warn("Failed to acquire distributed lock for key={}", lockKey);
                throw new BusinessException(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT);
            }
            task.run();
        } catch(InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
            throw new BusinessException(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT);
        } finally {
            if(acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
