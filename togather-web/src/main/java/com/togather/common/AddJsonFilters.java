package com.togather.common;

import org.aspectj.lang.JoinPoint;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to add custom json filters to Response.
 * Add custom filters of class {@code ResponseFilter} to variable "filters".
 * The actual application of these filters will be done by {@link ResponseDecorationAspect#decorateResponse(JoinPoint, MappingJacksonValue)}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddJsonFilters {
    ResponseFilter[] filters();
}
