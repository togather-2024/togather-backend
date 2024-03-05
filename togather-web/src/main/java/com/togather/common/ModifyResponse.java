package com.togather.common;

import com.togather.partyroom.core.model.FieldFilteringPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModifyResponse {
    FieldFilteringPolicy policy();
}
