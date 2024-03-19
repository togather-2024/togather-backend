package com.togather.partyroom.payment.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.payment.converter.PaymentConverter;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentDto;
import com.togather.partyroom.payment.repository.PaymentRepository;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberConverter memberConverter;
    private final PartyRoomReservationService partyRoomReservationService;
    private final PaymentConverter paymentConverter;

    @Transactional
    public PaymentDto saveRequiredData(PaymentDto.Request paymentDto, MemberDto memberDto) {

        PartyRoomReservation partyRoomReservation = partyRoomReservationService.findByReservationId(paymentDto.getReservationId());

        if (partyRoomReservation.getTotalPrice() != paymentDto.getAmount())
            throw new RuntimeException("mismatched amount with totalPrice");

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
}
