package com.togather.common.controller;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    /**
     * This exception is a business exception that must be handled by the client. We 'expect' these exceptions
     * and intentionally throw this error response, so the status code is 200 and log level is info.
     *
     * @param togatherApiException: the EXPECTED exception
     * @return Error code and message
     */
    @ExceptionHandler(TogatherApiException.class)
    @ResponseStatus(HttpStatus.OK)
    public ErrorResponse togatherApiException(TogatherApiException togatherApiException) {
        String errorCode = togatherApiException.getErrorCode().name();
        String message = messageSource.getMessage(togatherApiException.getErrorCode().getMessageKey(), null, null);

        log.info("[GlobalExceptionHandler] togatherApi business exception. code: {}, message: {}", errorCode, message);

        return new ErrorResponse(errorCode, message);
    }

    /**
     * This method is called when a runtime exception that has not been handled occurs. We did not expect this exception
     * to be occured. (E.g. db query exception, s3 connection, Null pointer exception) Therefore log level is error and
     * http status is 500.
     *
     * @param runtimeException
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeException(RuntimeException runtimeException) {
        String errorCode = runtimeException.getCause().toString();
        String message = runtimeException.getMessage();

        log.error("[GlobalExceptionHandler] unhandled runtime exception. code: {}, message: {}", errorCode, message);
        return new ErrorResponse(errorCode, message);
    }

    /**
     * Same method as above, but the only difference is that captured exception is checked exception, that is thrown in controller.
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse checkedException(Exception exception) {
        String errorCode = ErrorCode.INTERNAL_SERVER_ERROR.name();
        String message = messageSource.getMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey(), null, null);

        log.error("[GlobalExceptionHandler] unhandled checked exception. code: {}, message: {}", errorCode, exception.getMessage());
        return new ErrorResponse(errorCode, message);
    }
}
