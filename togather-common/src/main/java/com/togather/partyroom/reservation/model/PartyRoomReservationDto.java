package com.togather.partyroom.reservation.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Builder
    public static class Simple {

        @JsonProperty
        private long reservationId;

        @JsonProperty
        private int guestCount;

        @JsonProperty
        private LocalDateTime startTime;

        @JsonProperty
        private LocalDateTime endTime;

        @JsonProperty
        private PaymentStatus paymentStatus;

        @JsonProperty
        private String partyRoomName;
    }
}
