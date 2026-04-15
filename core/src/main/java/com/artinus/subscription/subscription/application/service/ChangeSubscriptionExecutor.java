package com.artinus.subscription.subscription.application.service;

import com.artinus.subscription.channel.application.port.out.LoadChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.ErrorCode;
import com.artinus.subscription.member.application.port.out.LoadMemberPort;
import com.artinus.subscription.member.application.port.out.SaveMemberPort;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.application.port.out.GetRandomResultPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistory;
import com.artinus.subscription.subscription.domain.SubscriptionHistoryEvent;
import com.artinus.subscription.subscription.domain.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class ChangeSubscriptionExecutor {

    private final LoadMemberPort loadMemberPort;
    private final SaveMemberPort saveMemberPort;
    private final LoadChannelPort loadChannelPort;
    private final GetRandomResultPort getRandomResultPort;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void execute(ChangeSubscriptionCommand command) {
        Channel channel = loadChannelPort.findById(command.getChannelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

        Optional<Member> memberOpt = loadMemberPort.findByPhoneNumber(command.getPhoneNumber());
        SubscriptionStatus currentStatus = memberOpt.map(Member::getSubscriptionStatus)
                .orElse(SubscriptionStatus.NONE);
        SubscriptionStatus targetStatus = command.getTargetStatus();

        Member member;
        if(currentStatus.isUpgradeTo(targetStatus)) {
            member = handleSubscribe(command, channel, memberOpt, currentStatus, targetStatus);
        } else {
            member = handleUnsubscribe(command, channel, memberOpt, currentStatus, targetStatus);
        }

        if(!getRandomResultPort.getRandomResult()) {
            log.info("csrng returned 0, rolling back subscription change for phoneNumber={}",
                    command.getPhoneNumber());
            throw new BusinessException(ErrorCode.SUBSCRIPTION_RANDOM_ROLLBACK);
        }

        saveMemberPort.updateStatus(command.getPhoneNumber(), targetStatus);

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


    private Member handleUnsubscribe(ChangeSubscriptionCommand command, Channel channel,
            Optional<Member> memberOpt, SubscriptionStatus currentStatus,
            SubscriptionStatus targetStatus) {
        if(!channel.canUnsubscribe()) {
            throw new BusinessException(ErrorCode.CHANNEL_UNSUBSCRIBE_NOT_ALLOWED);
        }
        Member member = memberOpt.orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        if(!currentStatus.canUnsubscribeTo(targetStatus)) {
            throw new BusinessException(ErrorCode.INVALID_SUBSCRIPTION_TRANSITION,
                    currentStatus.name(), targetStatus.name());
        }
        return member;
    }
}
