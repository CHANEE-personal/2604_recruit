package com.artinus.subscription.common.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 분산 락을 적용할 메서드에 선언하는 어노테이션. key는 SpEL 표현식으로 락 키를 지정한다. (예: "#command.phoneNumber") 락은 트랜잭션보다 먼저
 * 획득되고 나중에 해제된다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {

    /**
     * 락 키를 결정하는 SpEL 표현식
     */
    String key();

    /**
     * 락 획득 대기 시간 (초)
     */
    long waitTime() default 5;

    /**
     * 락 자동 해제 시간 (초, 데드락 방지) waitTime + 외부 API 최대 응답 시간(csrng: retry 3회 × 6s = ~20s)보다 크게 설정
     */
    long leaseTime() default 30;
}
