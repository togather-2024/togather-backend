package com.togather.partyroom.payment.model;

public class PaymentSuccessDto {

        String mid; //상점명
        String version;
        String paymentKey;
        String orderId;
        String orderName;
        String currency; //통화
        String method;
        String totalAmount;
        String balanceAmount; //취소가능금액(잔고)
        String suppliedAmount; //공급가액
        String vat;
        String status; //결제 처리 상태
        String requestedAt; //결제 시도 시간
        String approvedAt; //결제 승인 시간
        String useEscrow;
        String cultureExpense;
        PaymentSuccessCardDto card;
        String type;

    private class PaymentSuccessCardDto {
        String company; //회사명
        String number; //카드번호
        String installmentPlanMonths; //할부개월
        String isInterestFree;
        String approveNo; //승인번호
        String useCardPoint;
        String cardType;
        String ownerType;
        String acquireStatus; //승인상태
        String receiptUrl; //영수증 url
    }

}
