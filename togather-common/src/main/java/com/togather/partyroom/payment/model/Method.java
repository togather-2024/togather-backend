package com.togather.partyroom.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum Method {
    CARD("카드"), VIRTUAL("가상계좌"), MOBILE_PHONE("휴대폰"), SIMPLE("간편결제"), NONE("")
    ;

    private final String description;

    Method(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Method from(String method) {
        return Stream.of(Method.values())
                .filter(m -> m.getDescription().equals(method))
                .findFirst()
                .orElse(NONE);
    }

}
