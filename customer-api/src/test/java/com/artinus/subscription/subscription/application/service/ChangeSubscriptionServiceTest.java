package com.artinus.subscription.subscription.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.artinus.subscription.channel.application.port.out.LoadChannelPort;
import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.channel.domain.enums.ChannelType;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.member.application.port.out.LoadMemberPort;
import com.artinus.subscription.member.application.port.out.SaveMemberPort;
import com.artinus.subscription.member.application.port.out.UpdateMemberStatusPort;
import com.artinus.subscription.member.domain.Member;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.application.port.out.GetRandomResultPort;
import com.artinus.subscription.subscription.domain.SubscriptionHistoryEvent;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@ExtendWith(MockitoExtension.class)
class ChangeSubscriptionServiceTest {

    @InjectMocks
    private ChangeSubscriptionService service;

    @Mock
    private LoadMemberPort loadMemberPort;
    @Mock
    private SaveMemberPort saveMemberPort;
    @Mock
    private UpdateMemberStatusPort updateMemberStatusPort;
    @Mock
    private LoadChannelPort loadChannelPort;
    @Mock
    private GetRandomResultPort getRandomResultPort;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Channel bothChannel;
    private Channel subscribeOnlyChannel;
    private Channel unsubscribeOnlyChannel;
    private Member noneStatusMember;
    private Member basicStatusMember;
    private Member premiumStatusMember;


    @BeforeEach
    void setUp() {
        bothChannel = Channel.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();

        subscribeOnlyChannel = Channel.builder()
                .id(2L)
                .name("구독 전용 채널")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build();

        unsubscribeOnlyChannel = Channel.builder()
                .id(3L)
                .name("해지 전용 채널")
                .channelType(ChannelType.UNSUBSCRIBE_ONLY)
                .build();

        noneStatusMember = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build();

        basicStatusMember = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.BASIC)
                .build();

        premiumStatusMember = Member.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.PREMIUM)
                .build();
    }


    @Test
    @DisplayName("NONE → BASIC 구독에 성공한다")
    void subscribe_none_to_basic_success() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.BASIC);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(noneStatusMember));
        given(getRandomResultPort.getRandomResult()).willReturn(true);

        service.changeSubscription(command);

        verify(updateMemberStatusPort).updateStatus("01012345678", SubscriptionStatus.BASIC);
        verify(eventPublisher).publishEvent(any(SubscriptionHistoryEvent.class));
    }


    @Test
    @DisplayName("BASIC → PREMIUM 업그레이드에 성공한다")
    void subscribe_basic_to_premium_success() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.PREMIUM);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(basicStatusMember));
        given(getRandomResultPort.getRandomResult()).willReturn(true);

        service.changeSubscription(command);

        verify(updateMemberStatusPort).updateStatus("01012345678", SubscriptionStatus.PREMIUM);
        verify(eventPublisher).publishEvent(any(SubscriptionHistoryEvent.class));
    }


    @Test
    @DisplayName("PREMIUM → BASIC 다운그레이드에 성공한다")
    void unsubscribe_premium_to_basic_success() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.BASIC);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(premiumStatusMember));
        given(getRandomResultPort.getRandomResult()).willReturn(true);

        service.changeSubscription(command);

        verify(updateMemberStatusPort).updateStatus("01012345678", SubscriptionStatus.BASIC);
        verify(eventPublisher).publishEvent(any(SubscriptionHistoryEvent.class));
    }


    @Test
    @DisplayName("PREMIUM → NONE 완전 해지에 성공한다")
    void unsubscribe_premium_to_none_success() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.NONE);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(premiumStatusMember));
        given(getRandomResultPort.getRandomResult()).willReturn(true);

        service.changeSubscription(command);

        verify(updateMemberStatusPort).updateStatus("01012345678", SubscriptionStatus.NONE);
        verify(eventPublisher).publishEvent(any(SubscriptionHistoryEvent.class));
    }


    @Test
    @DisplayName("신규 회원은 자동 생성 후 구독한다")
    void subscribe_new_member_auto_created() {
        ChangeSubscriptionCommand command = command("01099998888", 1L, SubscriptionStatus.BASIC);
        Member newMember = Member.create("01099998888");

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01099998888")).willReturn(Optional.empty());
        given(saveMemberPort.save(any())).willReturn(newMember);
        given(getRandomResultPort.getRandomResult()).willReturn(true);

        service.changeSubscription(command);

        verify(saveMemberPort).save(any());
        verify(eventPublisher).publishEvent(any(SubscriptionHistoryEvent.class));
    }


    @Test
    @DisplayName("채널을 찾을 수 없으면 예외가 발생한다")
    void changeSubscription_channel_not_found() {
        ChangeSubscriptionCommand command = command("01012345678", 99L, SubscriptionStatus.BASIC);

        given(loadChannelPort.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.CHANNEL_NOT_FOUND));
    }


    @Test
    @DisplayName("구독 불가 채널에 구독 시도 시 예외가 발생한다")
    void subscribe_channel_not_allowed() {
        ChangeSubscriptionCommand command = command("01012345678", 3L, SubscriptionStatus.BASIC);

        given(loadChannelPort.findById(3L)).willReturn(Optional.of(unsubscribeOnlyChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(noneStatusMember));

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.CHANNEL_SUBSCRIBE_NOT_ALLOWED));
    }


    @Test
    @DisplayName("해지 불가 채널에 해지 시도 시 예외가 발생한다")
    void unsubscribe_channel_not_allowed() {
        ChangeSubscriptionCommand command = command("01012345678", 2L, SubscriptionStatus.NONE);

        given(loadChannelPort.findById(2L)).willReturn(Optional.of(subscribeOnlyChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(premiumStatusMember));

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.CHANNEL_UNSUBSCRIBE_NOT_ALLOWED));
    }


    @Test
    @DisplayName("미존재 회원 해지 시도 시 예외가 발생한다")
    void unsubscribe_member_not_found() {
        ChangeSubscriptionCommand command = command("01099998888", 1L, SubscriptionStatus.NONE);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01099998888")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.MEMBER_NOT_FOUND));
    }


    @Test
    @DisplayName("동일 상태로 변경 시도 시 예외가 발생한다")
    void changeSubscription_same_status_transition() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.PREMIUM);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(premiumStatusMember));

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.INVALID_SUBSCRIPTION_TRANSITION));
    }


    @Test
    @DisplayName("랜덤 롤백 발생 시 예외가 발생한다")
    void changeSubscription_random_rollback() {
        ChangeSubscriptionCommand command = command("01012345678", 1L, SubscriptionStatus.BASIC);

        given(loadChannelPort.findById(1L)).willReturn(Optional.of(bothChannel));
        given(loadMemberPort.findByPhoneNumber("01012345678")).willReturn(
                Optional.of(noneStatusMember));
        given(getRandomResultPort.getRandomResult()).willReturn(false);

        assertThatThrownBy(() -> service.changeSubscription(command)).isInstanceOf(
                        BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode()).isEqualTo(
                        ErrorCode.SUBSCRIPTION_RANDOM_ROLLBACK));
    }


    private ChangeSubscriptionCommand command(String phoneNumber, Long channelId,
            SubscriptionStatus targetStatus) {
        return ChangeSubscriptionCommand.builder()
                .phoneNumber(phoneNumber)
                .channelId(channelId)
                .targetStatus(targetStatus)
                .build();
    }
}
