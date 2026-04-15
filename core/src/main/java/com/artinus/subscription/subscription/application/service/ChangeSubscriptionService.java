package com.artinus.subscription.subscription.application.service;

import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionUseCase;
import com.artinus.subscription.subscription.application.port.out.DistributedLockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ChangeSubscriptionService implements ChangeSubscriptionUseCase {

    private static final String LOCK_KEY_PREFIX = "subscription:lock:";

    private final DistributedLockPort distributedLockPort;
    private final ChangeSubscriptionExecutor executor;


    @Override
    public void changeSubscription(ChangeSubscriptionCommand command) {
        command.validate();
        distributedLockPort.executeWithLock(LOCK_KEY_PREFIX + command.getPhoneNumber(),
                () -> executor.execute(command));
    }
}
