package com.togather.partyroom.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Method {
    CARD, CASH, TOSSPAY, NONE;

    @JsonCreator
    public static Method from(String method) {
        return Stream.of(Method.values())
                .filter(m -> m.toString().equals(method.toUpperCase()))
                .findFirst()
                .orElse(NONE);
    }

}
