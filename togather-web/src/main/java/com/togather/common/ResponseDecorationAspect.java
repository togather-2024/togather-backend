package com.togather.common;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.togather.member.model.MemberDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseDecorationAspect {

    @AfterReturning(pointcut = "@annotation(AddJsonFilters)", returning = "result")
    public void decorateResponse(JoinPoint joinPoint, MappingJacksonValue result) {
        ResponseFilter[] filters = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(AddJsonFilters.class).filters();

        SimpleFilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
        addCustomFilters(filterProvider, filters);

        result.setFilters(filterProvider);
    }

    private void addCustomFilters(SimpleFilterProvider filterProvider, ResponseFilter[] filters) {
        Arrays.stream(filters).forEach(filter -> {
            String filterId = extractFilterId(filter);
            PropertyFilter propertyFilter = createPropertyFilter(filter);
            filterProvider.addFilter(filterId, propertyFilter);
        });
    }

    private String extractFilterId(ResponseFilter filter) {
        return filter.getTargetClass().getAnnotation(JsonFilter.class).value();
    }

    private PropertyFilter createPropertyFilter(ResponseFilter filter) {
        return SimpleBeanPropertyFilter.serializeAllExcept(filter.getFieldsToExclude());
    }
}
