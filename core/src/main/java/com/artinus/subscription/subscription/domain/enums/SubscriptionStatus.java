package com.artinus.subscription.subscription.domain.enums;

public enum SubscriptionStatus {
    NONE(0), BASIC(1), PREMIUM(2);

    private final int level;


    SubscriptionStatus(int level) {
        this.level = level;
    }


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
        return this.level < target.level;
    }
}
