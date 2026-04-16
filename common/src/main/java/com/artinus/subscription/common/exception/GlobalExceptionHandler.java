package com.artinus.subscription.common.exception;

import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.artinus.subscription.common.exception.enums.ErrorCode;
import com.artinus.subscription.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        String message = messageSource.getMessage(e.getErrorCode()
                .getMessageCode(), e.getArgs(), LocaleContextHolder.getLocale());
        log.warn("BusinessException: {}", message, e);
        return ResponseEntity.status(e.getErrorCode()
                        .getHttpStatus())
                .body(ApiResponse.fail(message));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(message));
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException e) {
        String message = messageSource.getMessage("error.missing_parameter",
                new Object[] {e.getParameterName()}, LocaleContextHolder.getLocale());
        log.warn("Missing request parameter: {}", e.getParameterName());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(message));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        String message =
                messageSource.getMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageCode(), null,
                        LocaleContextHolder.getLocale());
        log.error("Unexpected exception", e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(message));
    }
}
