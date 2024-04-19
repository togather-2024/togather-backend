package com.togather.partyroom.reservation.service;

import com.togather.partyroom.payment.model.PaymentStatus;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.repository.PartyRoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyRoomReservationScheduleService {

    private final PartyRoomReservationRepository partyRoomReservationRepository;

    @Transactional
    public void removeExpiredReservation(long reservationId) {
        PartyRoomReservation reservation = partyRoomReservationRepository.findById(reservationId).orElse(null);
        if (reservation != null && reservation.getPaymentStatus() == PaymentStatus.PENDING) {
            //reservation.updatePaymentStatus(PaymentStatus.NOT_PAYED);
            partyRoomReservationRepository.updatePaymentStatus(reservationId, PaymentStatus.NOT_PAYED);
            log.info("[PartyRoomReservationService] changed status to NOT_PAYED for expired reservation. reservationId: {}", reservation.getReservationId());
        }
    }
}
