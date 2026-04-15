package com.artinus.subscription.subscription.domain;

public enum SubscriptionStatus {

    NONE, BASIC, PREMIUM;


    public boolean canSubscribeTo(SubscriptionStatus target) {
        if(this == NONE) {
            return target == BASIC || target == PREMIUM;
        }
        if(this == BASIC) {
            return target == PREMIUM;
        }
        // PREMIUM cannot subscribe further
        return false;
    }


    public boolean canUnsubscribeTo(SubscriptionStatus target) {
        if(this == PREMIUM) {
            return target == BASIC || target == NONE;
        }
        if(this == BASIC) {
            return target == NONE;
        }
        // NONE cannot unsubscribe further
        return false;
    }


    public boolean isUpgradeTo(SubscriptionStatus target) {
        return this.ordinal() < target.ordinal();
    }
}
