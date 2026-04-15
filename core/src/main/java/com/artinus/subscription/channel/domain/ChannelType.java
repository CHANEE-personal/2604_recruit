package com.artinus.subscription.channel.domain;

public enum ChannelType {

    BOTH, SUBSCRIBE_ONLY, UNSUBSCRIBE_ONLY;


    public boolean canSubscribe() {
        return this == BOTH || this == SUBSCRIBE_ONLY;
    }


    public boolean canUnsubscribe() {
        return this == BOTH || this == UNSUBSCRIBE_ONLY;
    }
}
