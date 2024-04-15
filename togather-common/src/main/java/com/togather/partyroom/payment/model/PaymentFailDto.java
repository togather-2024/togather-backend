package com.togather.partyroom.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailDto {
    private String errorCode;

    private String errorMessage;

    private String orderId;
}
