package com.artinus.subscription.subscription.adapter.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionCommand;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class SubscriptionRequest {

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리 숫자여야 합니다.")
    private String phoneNumber;

    @NotNull(message = "채널 ID는 필수입니다.")
    private Long channelId;

    @NotNull(message = "구독 상태는 필수입니다.")
    private SubscriptionStatus targetStatus;


    ChangeSubscriptionCommand toCommand() {
        return ChangeSubscriptionCommand.builder()
                .phoneNumber(phoneNumber)
                .channelId(channelId)
                .targetStatus(targetStatus)
                .build();
    }
}
