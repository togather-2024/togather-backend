package com.togather.partyroom.payment.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.payment.converter.PaymentConverter;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentDto;
import com.togather.partyroom.payment.model.PaymentSuccessDto;
import com.togather.partyroom.payment.model.TossPaymentConfig;
import com.togather.partyroom.payment.repository.PaymentRepository;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberConverter memberConverter;
    private final PartyRoomReservationService partyRoomReservationService;
    private final PaymentConverter paymentConverter;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
    public PaymentDto saveRequiredData(PaymentDto.Request paymentDto, MemberDto memberDto) {

        PartyRoomReservation partyRoomReservation = partyRoomReservationService.findByReservationId(paymentDto.getReservationId());

        if (partyRoomReservation.getTotalPrice() != paymentDto.getAmount())
            throw new RuntimeException("mismatched amount with totalPrice"); //TODO: exception class 수정하기

        Payment payment = Payment.builder()
                .orderName(paymentDto.getOrderName())
                .method(paymentDto.getMethod())
                .amount(paymentDto.getAmount())
                .customer(memberConverter.convertToEntity(memberDto))
                .build();

        paymentRepository.save(payment);

        log.info("save temporary Payment object: {}", payment.getPaymentId());

        PaymentDto responsePaymentDto = paymentConverter.convertToDto(payment);

        responsePaymentDto.setSuccessUrl(paymentDto.getSuccessUrl());
        responsePaymentDto.setFailUrl(paymentDto.getFailUrl());

        return responsePaymentDto;
    }

    @Transactional
    public PaymentSuccessDto verifySuccessfulTossPayment(String paymentKey, String orderId, long amount) {
        Payment payment = verifyPayment(orderId, amount);

        PaymentSuccessDto paymentSuccessDto = requestPaymentAccept(payment, paymentKey);
        payment.update(paymentKey);

        log.info("payment successful, paymentId: {}", payment.getPaymentId());

        partyRoomReservationService.updatePaymentStatusToComplete(payment);
        return paymentSuccessDto;
    }

    private Payment verifyPayment(String orderId, long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(RuntimeException::new); //TODO: Exception

        if (payment.getAmount() != amount)
            throw new RuntimeException(); //TODO: Exception

        return payment;
    }

    private PaymentSuccessDto requestPaymentAccept(Payment payment, String paymentKey) {
        HttpHeaders httpHeaders = getHeaders();

        WebClient webClient = WebClient.builder()
                .baseUrl(TossPaymentConfig.URL)
                .defaultHeaders(headers -> {
                    httpHeaders.forEach((key, value) -> {
                        headers.addAll(key, value);
                    });
                })
                .build();

        Map<String, Object> params = Map.of("paymentKey", paymentKey, "orderId", payment.getOrderId(), "amount", payment.getAmount());

        PaymentSuccessDto paymentSuccessDto;

        try {
            paymentSuccessDto = webClient.post()
                    //url: "https://api.tosspayments.com/v1/payments/confirm"
                    .uri(uriBuilder -> uriBuilder.path("confirm").build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(params)
                    .retrieve()
                    .bodyToMono(PaymentSuccessDto.class) //응답을 PaymentSuccessDto 클래스로 변환
                    .block();

        } catch (Exception e) {
            log.error("error: {}", e);
            throw new RuntimeException(e); //TODO: exception
        }

        return paymentSuccessDto;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String authValue = tossPaymentConfig.getTestSecretApiKey() + ":";
        String encodedAuthValue = Base64.getEncoder().encodeToString(authValue.getBytes(StandardCharsets.UTF_8)); //Base64로 인코딩

        httpHeaders.add("Authorization", "Basic " + encodedAuthValue); //Authorization 헤더 설정
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return httpHeaders;
    }

}