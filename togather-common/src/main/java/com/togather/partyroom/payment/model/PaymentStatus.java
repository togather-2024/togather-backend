package com.togather.partyroom.payment.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.stream.Stream;

public enum PaymentStatus {
    NOT_PAYED, PENDING, COMPLETE, CANCELED, NONE;

    @JsonCreator
    public static PaymentStatus from(String paymentStatus) {
        return Stream.of(PaymentStatus.values())
                .filter(status -> status.toString().equals(paymentStatus.toUpperCase()))
                .findFirst()
                .orElse(NONE);
    }
}
