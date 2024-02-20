package com.togather.partyroom.core.service;

import com.togather.partyroom.core.converter.PartyRoomOperationDayConverter;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.repository.PartyRoomOperationDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyRoomOperationDayService {
    private final PartyRoomOperationDayConverter partyRoomOperationDayConverter;
    private final PartyRoomOperationDayRepository partyRoomOperationDayRepository;

    public void registerOperationDay(PartyRoomOperationDayDto partyRoomOperationDayDto) {
        PartyRoomOperationDay operationDay = partyRoomOperationDayConverter.convertFromDto(partyRoomOperationDayDto);
        partyRoomOperationDayRepository.save(operationDay);
    }

    public void registerOperationDays(List<PartyRoomOperationDayDto> operationDayDtoList) {
        operationDayDtoList.stream()
                .forEach(this::registerOperationDay);
    }
}
