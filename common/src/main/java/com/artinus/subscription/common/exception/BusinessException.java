package com.artinus.subscription.common.exception;

import com.artinus.subscription.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;


    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.args = null;
    }


    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageCode());
        this.errorCode = errorCode;
        this.args = args;
    }
}
