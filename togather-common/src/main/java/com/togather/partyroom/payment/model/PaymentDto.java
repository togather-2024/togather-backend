package com.togather.partyroom.payment.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
public class PaymentDto {

    private long paymentId;

    private Method method;

    private long amount;

    private String orderName;

    private String orderId;

    private String paymentKey;

    private String customerEmail;

    private String customerName;

    private boolean isPaymentSuccess;

    private boolean isCanceled;

    private String cancelReason;

    private String failReason;

    private LocalDateTime createdAt;

    private String successUrl;

    private String failUrl;

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
}
