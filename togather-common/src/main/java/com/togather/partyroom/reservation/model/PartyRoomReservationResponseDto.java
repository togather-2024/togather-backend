package com.togather.partyroom.reservation.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
@JsonFilter("PartyRoomReservationResponseDtoFilter")
public class PartyRoomReservationResponseDto {

    private PartyRoomReservationDto partyRoomReservationDto;

    private PartyRoomLocationDto partyRoomLocationDto;

    private PartyRoomImageDto partyRoomImageDto;

    @Builder
    @Getter
    public static class AvailableTimes {
        private List<Integer> availableTimes;
    }
}
