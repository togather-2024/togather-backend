package com.togather.partyroom.payment.model;

import lombok.Getter;

import java.util.List;

@Getter
public class PaymentCancelDto {

    private List<CancelInfo> cancels;

    @Getter
    public static class Request {
        private String cancelReason;
    }

    @Getter
    public static class CancelInfo {
        private String cancelReason;
        private String canceledAt;
    }
}
