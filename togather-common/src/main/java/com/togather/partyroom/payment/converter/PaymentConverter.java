package com.togather.partyroom.payment.converter;

import com.togather.member.model.Member;
import com.togather.member.service.MemberService;
import com.togather.partyroom.payment.model.Method;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConverter {

    private final MemberService memberService;

    public Payment convertToEntity(PaymentDto paymentDto) {
        if (paymentDto == null)
            return null;

        Member findCustomer = memberService.findByEmail(paymentDto.getCustomerEmail());

        return Payment.allFields()
                .paymentId(paymentDto.getPaymentId())
                .orderId(paymentDto.getOrderId())
                .paymentKey(paymentDto.getPaymentKey())
                .orderName(paymentDto.getOrderName())
                .method(Method.from(paymentDto.getMethod()))
                .amount(paymentDto.getAmount())
                .isPaymentSuccess(paymentDto.isPaymentSuccess())
                .customer(findCustomer)
                .failReason(paymentDto.getFailReason())
                .isCanceled(paymentDto.isCanceled())
                .canceledAt(paymentDto.getCanceledAt())
                .cancelReason(paymentDto.getCancelReason())
                .build();
    }

    public PaymentDto convertToDto(Payment payment) {
        if (payment == null)
            return null;

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .method(payment.getMethod().getDescription())
                .amount(payment.getAmount())
                .orderName(payment.getOrderName())
                .orderId(payment.getOrderId())
                .paymentKey(payment.getPaymentKey())
                .isPaymentSuccess(payment.isPaymentSuccess())
                .customerEmail(payment.getCustomer().getEmail())
                .customerName(payment.getCustomer().getMemberName())
                .isCanceled(payment.isCanceled())
                .cancelReason(payment.getCancelReason())
                .canceledAt(payment.getCanceledAt())
                .failReason(payment.getFailReason())
                .createdAt(payment.getCreatedAt())
                .modifiedAt(payment.getModifiedAt())
                .build();
    }
}
