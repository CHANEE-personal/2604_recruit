package com.artinus.subscription.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.member.not_found"),

    // Channel
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "error.channel.not_found"),
    CHANNEL_SUBSCRIBE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "error.channel.subscribe_not_allowed"),
    CHANNEL_UNSUBSCRIBE_NOT_ALLOWED(HttpStatus.BAD_REQUEST,
            "error.channel.unsubscribe_not_allowed"),
    INVALID_CHANNEL_NAME(HttpStatus.BAD_REQUEST, "error.channel.invalid_name"),
    INVALID_CHANNEL_TYPE(HttpStatus.BAD_REQUEST, "error.channel.invalid_type"),

    // Subscription
    INVALID_SUBSCRIPTION_TRANSITION(HttpStatus.BAD_REQUEST,
            "error.subscription.invalid_transition"),
    SUBSCRIPTION_RANDOM_ROLLBACK(HttpStatus.INTERNAL_SERVER_ERROR,
            "error.subscription.random_rollback"),
    SUBSCRIPTION_LOCK_CONFLICT(HttpStatus.CONFLICT, "error.subscription.lock_conflict"),

    // External API
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "error.external_api"),

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal_server");

    private final HttpStatus httpStatus;
    private final String messageCode;
}
