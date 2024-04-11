package com.togather.partyroom.payment.model;

import lombok.Getter;

public class PaymentCancelDto {
    @Getter
    public static class Request {
        private String cancelReason;
    }

    @Getter
    public static class Response {
        private String cancelReason;
        private String canceledAt;
    }
}
