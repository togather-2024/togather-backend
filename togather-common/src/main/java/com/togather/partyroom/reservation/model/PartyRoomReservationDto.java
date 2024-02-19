package com.togather.partyroom.reservation.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PartyRoomReservationDto {
    private PartyRoomDto partyRoomDto;
    private MemberDto reservationGuest;
    private int guestCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PaymentStatus paymentStatus;
}
