package com.togather.common;

import com.togather.partyroom.core.model.FieldFilteringPolicy;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseDecorationAspect {
    private final ReflectiveObjectDecorator reflectiveObjectDecorator;

    @AfterReturning(pointcut = "@annotation(ModifyResponse)", returning = "result")
    public void modifyResponse(JoinPoint joinPoint, ResponseEntity result) throws NoSuchFieldException, IllegalAccessException {
        FieldFilteringPolicy policy = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ModifyResponse.class).policy();

        Object body = result.getBody();
        body = reflectiveObjectDecorator.applyPolicy(body, policy);


        Field bodyField = result.getClass().getSuperclass().getDeclaredField("body");
        bodyField.setAccessible(true);
        bodyField.set(result, body);
    }


}
