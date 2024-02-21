package com.togather.partyroom.payment.model;

import java.util.HashMap;

public enum PaymentStatus {
    NOT_PAYED, PENDING, COMPLETE, NONE;

    private static final HashMap<String, PaymentStatus> paymentStatuses = new HashMap<>();

    static {
        for (PaymentStatus paymentStatus : PaymentStatus.values())
            paymentStatuses.put(paymentStatus.name(), paymentStatus);
    }

    public static PaymentStatus from(String paymentStatus) {
        return paymentStatuses.getOrDefault(paymentStatus, NONE);
    }
}
