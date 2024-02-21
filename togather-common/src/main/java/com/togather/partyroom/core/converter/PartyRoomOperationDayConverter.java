package com.togather.partyroom.core.converter;

import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomOperationDayConverter {
    private final PartyRoomConverter partyRoomConverter;

    public PartyRoomOperationDay convertFromDto(PartyRoomOperationDayDto partyRoomOperationDayDto) {
        return PartyRoomOperationDay.builder()
                .operationDaysId(partyRoomOperationDayDto.getOperationDaysId())
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomOperationDayDto.getPartyRoomDto()))
                .operationDay(partyRoomOperationDayDto.getOperationDay())
                .build();
    }


}
