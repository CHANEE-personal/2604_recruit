package com.artinus.subscription.channel.domain.enums;

public enum ChannelType {

    BOTH, SUBSCRIBE_ONLY, UNSUBSCRIBE_ONLY;


    public boolean canSubscribe() {
        return this == BOTH || this == SUBSCRIBE_ONLY;
    }


    public boolean canUnsubscribe() {
        return this == BOTH || this == UNSUBSCRIBE_ONLY;
    }
}
