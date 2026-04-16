package com.artinus.subscription.channel.application.port.in;

import com.artinus.subscription.channel.domain.enums.ChannelType;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.enums.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChannelCommand {

    private final String name;
    private final ChannelType channelType;


    public void validate() {
        if(name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_CHANNEL_NAME);
        }
        if(channelType == null) {
            throw new BusinessException(ErrorCode.INVALID_CHANNEL_TYPE);
        }
    }
}
