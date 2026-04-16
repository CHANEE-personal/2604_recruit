package com.artinus.subscription.subscription.application.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.artinus.subscription.channel.application.port.out.LoadChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.common.lock.DistributedLock;
import com.artinus.subscription.member.application.port.out.LoadMemberPort;
import com.artinus.subscription.member.application.port.out.SaveMemberPort;
import com.artinus.subscription.member.application.port.out.UpdateMemberStatusPort;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionUseCase;
import com.artinus.subscription.subscription.application.port.out.GetRandomResultPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.SubscriptionHistoryEvent;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class ChangeSubscriptionService implements ChangeSubscriptionUseCase {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final UpdateMemberStatusPort updateMemberStatusPort;
    private final LoadChannelPort loadChannelPort;
    private final GetRandomResultPort getRandomResultPort;
    private final ApplicationEventPublisher eventPublisher;


    @DistributedLock(key = "#command.phoneNumber")
    @Override
    @Transactional
    public void changeSubscription(ChangeSubscriptionCommand command) {
        command.validate();

        Channel channel = loadChannelPort.findById(command.getChannelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

        Optional<Member> memberOpt = loadMemberPort.findByPhoneNumber(command.getPhoneNumber());
        SubscriptionStatus currentStatus = memberOpt.map(Member::getSubscriptionStatus)
                .orElse(SubscriptionStatus.NONE);
        SubscriptionStatus targetStatus = command.getTargetStatus();

        Member member;
        if(currentStatus.isUpgradeTo(targetStatus)) {
            member = this.handleSubscribe(command, channel, memberOpt, currentStatus, targetStatus);
        } else {
            member = this.handleUnsubscribe(channel, memberOpt, currentStatus, targetStatus);
        }

        if(!getRandomResultPort.getRandomResult()) {
            log.info("csrng returned 0, rolling back subscription change for phoneNumber={}",
                    command.getPhoneNumber());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_RANDOM_ROLLBACK);
        }

        updateMemberStatusPort.updateStatus(command.getPhoneNumber(), targetStatus);

        eventPublisher.publishEvent(new SubscriptionHistoryEvent(SubscriptionHistory.builder()
                .memberId(member.getId())
                .phoneNumber(command.getPhoneNumber())
                .channelId(channel.getId())
                .channelName(channel.getName())
                .previousStatus(currentStatus)
                .newStatus(targetStatus)
                .build()));

        log.info("Subscription changed: phoneNumber={}, channel={}, {} -> {}",
                command.getPhoneNumber(), channel.getName(), currentStatus, targetStatus);
    }


    private Member handleSubscribe(ChangeSubscriptionCommand command, Channel channel,
            Optional<Member> memberOpt, SubscriptionStatus currentStatus,
            SubscriptionStatus targetStatus) {
        if(!channel.canSubscribe()) {
            throw new BusinessException(ErrorCode.CHANNEL_SUBSCRIBE_NOT_ALLOWED);
        }
        if(!currentStatus.canSubscribeTo(targetStatus)) {
            throw new BusinessException(ErrorCode.INVALID_SUBSCRIPTION_TRANSITION,
                    currentStatus.name(), targetStatus.name());
        }
        return memberOpt.orElseGet(
                () -> saveMemberPort.save(Member.create(command.getPhoneNumber())));
    }


    private Member handleUnsubscribe(Channel channel, Optional<Member> memberOpt,
            SubscriptionStatus currentStatus, SubscriptionStatus targetStatus) {
        if(!channel.canUnsubscribe()) {
            throw new BusinessException(ErrorCode.CHANNEL_UNSUBSCRIBE_NOT_ALLOWED);
        }
        Member member =
                memberOpt.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if(!currentStatus.canUnsubscribeTo(targetStatus)) {
            throw new BusinessException(ErrorCode.INVALID_SUBSCRIPTION_TRANSITION,
                    currentStatus.name(), targetStatus.name());
        }
        return member;
    }
}
