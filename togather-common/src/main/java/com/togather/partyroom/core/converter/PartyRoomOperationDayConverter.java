package com.togather.partyroom.core.converter;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    public PartyRoomOperationDayDto convertFromEntity(PartyRoomOperationDay partyRoomOperationDay) {
        if (partyRoomOperationDay == null) {
            return null;
        }
        return PartyRoomOperationDayDto.builder()
                .operationDaysId(partyRoomOperationDay.getOperationDaysId())
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomOperationDay.getPartyRoom()))
                .operationDay(partyRoomOperationDay.getOperationDay())
                .build();
    }

    public List<PartyRoomOperationDayDto> convertFromEntity(List<PartyRoomOperationDay> operationDays) {
        return operationDays.stream().map(this::convertFromEntity).collect(Collectors.toList());
    }


}
