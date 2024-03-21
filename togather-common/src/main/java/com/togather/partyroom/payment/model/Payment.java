package com.togather.partyroom.payment.model;

import com.togather.common.model.BaseTimeEntity;
import com.togather.member.model.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;

    @Column(name = "order_id")
    private String orderId; //주문ID, UNIQUE

    @Column(name = "payment_key")
    private String paymentKey; //결제 key값, UNIQUE

    @Column(name = "order_name")
    private String orderName; //예약 상품

    @Column(name = "method", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private Method method; //결제 수단

    @Column(name = "amount")
    private long amount; //총 결제금액

    @Column(name = "is_payment_success")
    private boolean isPaymentSuccess; //성공 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_srl")
    private Member customer; //예약자

    @Column(name = "fail_reason")
    private String failReason; //실패 사유

    @Column(name = "is_canceled")
    private boolean isCanceled; //취소 여부

    @Column(name = "cancel_reason")
    private String cancelReason; //취소 사유

    @Builder
    public Payment(String orderName, Method method, long amount, Member customer) {
        this.orderId = UUID.randomUUID().toString();
        this.orderName = orderName;
        this.method = method;
        this.amount = amount;
        this.isPaymentSuccess = false;
        this.customer = customer;
    }

    public void update(String paymentKey) {
        this.paymentKey = paymentKey;
        this.isPaymentSuccess = true;
    }
}
