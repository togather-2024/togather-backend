package com.togather.partyroom.core.model;

import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;

@Getter
@Builder
public class PartyRoomOperationDayDto {
    private PartyRoomDto partyRoomDto;
    private DayOfWeek operationDay;
}
