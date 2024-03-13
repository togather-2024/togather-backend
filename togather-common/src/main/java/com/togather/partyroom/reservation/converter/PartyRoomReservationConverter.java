package com.togather.partyroom.reservation.converter;

import com.togather.member.converter.MemberConverter;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.payment.model.PaymentStatus;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomReservationConverter {

    private final PartyRoomConverter partyRoomConverter;
    private final MemberConverter memberConverter;

    public PartyRoomReservation convertToEntity(PartyRoomReservationDto partyRoomReservationDto) {
        return PartyRoomReservation.builder()
                .reservationId(partyRoomReservationDto.getReservationId())
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomReservationDto.getPartyRoomDto()))
                .reservationGuest(memberConverter.convertToEntity(partyRoomReservationDto.getReservationGuestDto()))
                .guestCount(partyRoomReservationDto.getGuestCount())
                .startTime(partyRoomReservationDto.getStartTime())
                .endTime(partyRoomReservationDto.getEndTime())
                .paymentStatus(PaymentStatus.from(String.valueOf(partyRoomReservationDto.getPaymentStatus())))
                .bookedDate(partyRoomReservationDto.getBookedDate())
                .totalPrice(partyRoomReservationDto.getTotalPrice())
                .build();

    }

    public PartyRoomReservationDto convertToDto(PartyRoomReservation partyRoomReservation) {
        return PartyRoomReservationDto.builder()
                .reservationId(partyRoomReservation.getReservationId())
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomReservation.getPartyRoom()))
                .reservationGuestDto(memberConverter.convertToDto(partyRoomReservation.getReservationGuest()))
                .startTime(partyRoomReservation.getStartTime())
                .endTime(partyRoomReservation.getEndTime())
                .paymentStatus(partyRoomReservation.getPaymentStatus())
                .bookedDate(partyRoomReservation.getBookedDate())
                .totalPrice(partyRoomReservation.getTotalPrice())
                .build();
    }
}
