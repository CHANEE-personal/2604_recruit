package com.artinus.subscription.lock;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.common.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String LOCK_KEY_PREFIX = "lock:";

    private final RedissonClient redissonClient;
    private final ExpressionParser parser = new SpelExpressionParser();


    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock)
            throws Throwable {
        String lockKey = LOCK_KEY_PREFIX + resolveKey(joinPoint, distributedLock.key());

        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
                    TimeUnit.SECONDS);
            if(!acquired) {
                log.warn("Failed to acquire distributed lock for key={}", lockKey);
                throw new BusinessException(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT);
            }
            return joinPoint.proceed();
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.SUBSCRIPTION_LOCK_CONFLICT);
        } finally {
            if(acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    private String resolveKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        StandardEvaluationContext context = new StandardEvaluationContext();
        for(int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
