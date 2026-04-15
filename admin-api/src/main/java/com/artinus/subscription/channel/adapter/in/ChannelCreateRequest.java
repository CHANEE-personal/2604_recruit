package com.artinus.subscription.channel.adapter.in;

import com.artinus.subscription.channel.application.port.in.CreateChannelCommand;
import com.artinus.subscription.channel.domain.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class ChannelCreateRequest {

    @NotBlank(message = "채널 이름은 필수입니다.")
    private String name;

    @NotNull(message = "채널 타입은 필수입니다.")
    private ChannelType channelType;


    CreateChannelCommand toCommand() {
        return CreateChannelCommand.builder()
                .name(name)
                .channelType(channelType)
                .build();
    }
}
