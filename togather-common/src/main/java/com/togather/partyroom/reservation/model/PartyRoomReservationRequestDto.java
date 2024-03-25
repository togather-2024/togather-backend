package com.togather.partyroom.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyRoomReservationRequestDto {
    private long partyRoomId;

    private int guestCount;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long totalPrice;
}
