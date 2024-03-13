package com.togather.partyroom.reservation.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@JsonFilter("PartyRoomReservationDtoFilter")
public class PartyRoomReservationDto {

    private long reservationId;

    @Setter
    private PartyRoomDto partyRoomDto;

    @Setter
    private MemberDto reservationGuestDto;

    private int guestCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private PaymentStatus paymentStatus;

    @Setter
    private LocalDateTime bookedDate;

    private long totalPrice;

}
