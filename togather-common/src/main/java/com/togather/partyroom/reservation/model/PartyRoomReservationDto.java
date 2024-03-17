package com.togather.partyroom.reservation.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyRoomReservationDto {

    private long reservationId;

    private PartyRoomDto partyRoomDto;

    private MemberDto reservationGuestDto;

    private int guestCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private PaymentStatus paymentStatus;

    private LocalDateTime bookedDate;

    private long totalPrice;
}
