package com.togather.common.request;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.common.util.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class TimeZoneRequestBodyAdvice implements RequestBodyAdvice {

    /**
     * Always return true, so applied to every API calls (every controller methods)
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * Nothing changed
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    /**
     * Changes the input for fields with class 'LocalDateTime' to a value of same time in KST timezone
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        List<Field> fieldList = Arrays.asList(body.getClass().getDeclaredFields());
        fieldList.stream()
                .filter(field -> field.getType() == LocalDateTime.class)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        field.set(body, LocalDateTimeUtils.convertToKstFromUtc((LocalDateTime) field.get(body)));
                    } catch (IllegalAccessException e) {
                        // This exception can never been thrown because field is setAccessible to true
                        log.error("[TimeZoneRequestBodyAdvice] error occurred while handling parameters with localdatetime. method: {}", parameter.getMethod().getName());
                        throw new TogatherApiException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                });
        ;

        return body;
    }


    /**
     * Nothing changed
     */
    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return null;
    }
}
