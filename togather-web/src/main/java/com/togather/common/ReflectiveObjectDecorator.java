package com.togather.common;

import com.togather.partyroom.core.model.FieldFilteringPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class ReflectiveObjectDecorator {

    public <T> T applyPolicy(T target, FieldFilteringPolicy policy) {
        if (target instanceof Collection<?>) {
            return (T)((Collection) target).stream().map(element -> applyPolicy(element, policy)).toList();
        }

        if (!policy.getClazz().isInstance(target)) {
            log.error("Cannot apply policy to target: {}, using policy: {}", target, policy);
            return target;
        }
        Arrays.stream(policy.getFieldsToExclude().split(","))
                .forEach(fieldName -> {
                    modifyField(target, fieldName, null);
                });

        if (!CollectionUtils.isEmpty(policy.getChildFieldPolicies())) {
            policy.getChildFieldPolicies().stream()
                    .forEach(child -> {
                        String field = child.getFirst();
                        FieldFilteringPolicy childPolicy = child.getSecond();
                        modifyField(target, field, applyPolicy(getValueOfField(target, field), childPolicy));
                    });
        }

        return target;
    }

    /**
     * Changes the specific field of object({@code subject}) to value
     * @param subject : The target object of modification
     * @param fieldName : The fieldName of modification
     * @param value : The {@code fieldName} of {@code target} becomes {@code value}
     * @param <T> : Type of subject
     */
    private <T> void modifyField(T subject, String fieldName, Object value) {
        try {
            Field field = subject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(subject, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {

        }
    }

    private <T> Object getValueOfField(T subject, String fieldName) {
        try {
            Field field = subject.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(subject);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }


}
