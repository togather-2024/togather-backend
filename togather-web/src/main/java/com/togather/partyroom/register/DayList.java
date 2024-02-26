package com.togather.partyroom.register;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DayListValidator.class)
public @interface DayList {
    String message() default "should contain only valid dayOfWeek String";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
