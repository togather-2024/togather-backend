package com.togather.partyroom.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Builder
public class PartyRoomOperationDayDto {
    private long operationDaysId;
    @Setter
    private PartyRoomDto partyRoomDto;
    private DayOfWeek operationDay;
}
