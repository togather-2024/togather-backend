package com.togather.partyroom.reservation.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.payment.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PartyRoomReservationDto {

    private long reservationId;

    private long partyRoomId; //Request

    private PartyRoomDto partyRoomDto; //Response

    private long reservationGuestSrl; //Request

    private MemberDto reservationGuest; //Response

    private int guestCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private PaymentStatus paymentStatus;

}
