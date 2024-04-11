package com.togather.partyroom.payment.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.payment.converter.PaymentConverter;
import com.togather.partyroom.payment.model.*;
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
import java.util.UUID;

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
                .partyRoomReservation(partyRoomReservation)
                .build();

        paymentRepository.save(payment);

        log.info("save temporary Payment object: {}", payment.getPaymentId());

        PaymentDto responsePaymentDto = paymentConverter.convertToDto(payment);

        responsePaymentDto.setSuccessUrl(paymentDto.getSuccessUrl());
        responsePaymentDto.setFailUrl(paymentDto.getFailUrl());

        return responsePaymentDto;
    }

    @Transactional
    public PaymentSuccessDto handleSuccessTossPayment(String paymentKey, String orderId, long amount) {
        Payment payment = verifyPayment(orderId, amount);

        PaymentSuccessDto paymentSuccessDto = requestPaymentAccept(payment, paymentKey);
        payment.setPaymentSuccess(paymentKey);

        log.info("payment successful for orderId: {}", payment.getOrderId());

        partyRoomReservationService.updatePaymentStatus(payment, PaymentStatus.COMPLETE);
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
            deletePayment(payment);

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

    @Transactional
    public PaymentFailDto handleFailedTossPayment(String code, String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(RuntimeException::new);//TODO: exception

        payment.setPaymentFailed(message);

        log.info("payment failed for orderId: {}, reason: {}", payment.getOrderId(), message);

        deletePayment(payment);

        return PaymentFailDto.builder()
                .errorCode(code)
                .errorMessage(message)
                .orderId(orderId)
                .build();
    }

    public PaymentDto inquiryPayment(String paymentKey) {
        PaymentDto paymentDto = paymentConverter.convertToDto(paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new RuntimeException("can not found payment"))); //TODO: exception

        log.info("retrieve payment detail for paymentKey: {}", paymentKey);

        return paymentDto;
    }

    @Transactional
    public String cancelPayment(String paymentKey, PaymentCancelDto.Request paymentCancelDto) {
        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(RuntimeException::new);//TODO: exception

        HttpHeaders httpHeaders = getHeaders();
        httpHeaders.add("Idempotency-Key", UUID.randomUUID().toString()); //멱등키 추가로 중복 요청 방지

        WebClient webClient = WebClient.builder()
                .baseUrl(TossPaymentConfig.URL)
                .defaultHeaders(headers -> {
                    httpHeaders.forEach((key, value) -> {
                        headers.addAll(key, value);
                    });
                })
                .build();

        Map<String, String> param = Map.of("cancelReason", paymentCancelDto.getCancelReason());

        PaymentCancelDto paymentCancelResponseDto;
        try {
            paymentCancelResponseDto = webClient.post()
                    //url: "https://api.tosspayments.com/v1/payments/{paymentKey}/cancel"
                    .uri(uriBuilder -> uriBuilder.path(paymentKey + "/cancel").build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(param)
                    .retrieve()
                    .bodyToMono(PaymentCancelDto.class) //응답을 PaymentCancelDto 클래스로 변환
                    .block();
        } catch (Exception e) {
            log.error("error: {}", e);
            throw new RuntimeException(e); //TODO: exception
        }

        payment.setPaymentCancelInfo(paymentCancelResponseDto);

        log.info("cancel payment for paymentKey: {}", paymentKey);
        log.info("cancelReason: {}", payment.getCancelReason());

        partyRoomReservationService.updatePaymentStatus(payment, PaymentStatus.CANCELED);

        return payment.getCancelReason();
    }

    private void deletePayment(Payment payment) {
        paymentRepository.delete(payment);
        log.info("delete paymentId: {}", payment.getPaymentId());
    }
}
