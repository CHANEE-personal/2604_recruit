package com.artinus.subscription.subscription.application.port.in;

import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeSubscriptionCommand {

    private final String phoneNumber;
    private final Long channelId;
    private final SubscriptionStatus targetStatus;


    public void validate() {
        if(phoneNumber == null || phoneNumber.isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "phoneNumber");
        }
        if(channelId == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "channelId");
        }
        if(targetStatus == null) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING, "targetStatus");
        }
    }
}
