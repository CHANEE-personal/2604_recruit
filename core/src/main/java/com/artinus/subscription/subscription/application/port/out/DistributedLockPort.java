package com.artinus.subscription.subscription.application.port.out;

public interface DistributedLockPort {

    /**
     * 분산 락을 획득한 뒤 task를 실행하고 락을 해제한다.
     * 락 획득 대기 시간 초과 시 BusinessException(SUBSCRIPTION_LOCK_CONFLICT)을 던진다.
     */
    void executeWithLock(String lockKey, Runnable task);
}
