package com.togather.partyroom.payment.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


public class PaymentDto {
    @Getter
    @Builder
    public static class Request {
        @NotNull
        private Method method;

        @NotNull
        private long amount;

        @NotNull
        private String orderName;

        private String successUrl;

        private String failUrl;

    }
    @Getter
    @Builder
    public static class Response {
        private String method;

        private long amount;

        private String orderName;

        private String orderId;

        private String guestEmail;

        private String guestName;

        private String successUrl;

        private String failUrl;

        private boolean isCanceled;

        private String cancelReason;

        private String failReason;

        private LocalDateTime createdAt;
    }
}
