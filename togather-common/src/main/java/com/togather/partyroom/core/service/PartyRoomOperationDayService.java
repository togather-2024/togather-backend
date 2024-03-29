package com.togather.partyroom.core.service;

import com.togather.partyroom.core.converter.PartyRoomOperationDayConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.repository.PartyRoomOperationDayRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyRoomOperationDayService {
    private final PartyRoomOperationDayConverter partyRoomOperationDayConverter;
    private final PartyRoomOperationDayRepository partyRoomOperationDayRepository;

    public List<PartyRoomOperationDayDto> findOperationDaysByPartyRoom(PartyRoom partyRoom) {
        return partyRoomOperationDayConverter.convertFromEntity(partyRoomOperationDayRepository.findByPartyRoom(partyRoom));
    }

    public void deleteOperationDay(PartyRoomOperationDayDto partyRoomOperationDayDto) {
        partyRoomOperationDayRepository.delete(partyRoomOperationDayConverter.convertFromDto(partyRoomOperationDayDto));
    }

    public void deleteAllOperationDaysByPartyRoom(PartyRoom partyroom) {
        int deletedOperationDayCount = partyRoomOperationDayRepository.deleteAllByPartyRoom(partyroom);
        log.info("[PartyRoomOperationDayService - deleteAll] successfully deleted {} operationDay for partyRoomId: {}", deletedOperationDayCount, partyroom.getPartyRoomId());
    }

    public void registerOperationDay(PartyRoomOperationDayDto partyRoomOperationDayDto) {
        PartyRoomOperationDay operationDay = partyRoomOperationDayConverter.convertFromDto(partyRoomOperationDayDto);
        partyRoomOperationDayRepository.save(operationDay);
    }

    public void registerOperationDays(List<PartyRoomOperationDayDto> operationDayDtoList) {
        operationDayDtoList.stream()
                .forEach(this::registerOperationDay);
    }

    // Modify PartyRoom Operation Day Data
    // If beforeDays = ["MONDAY", "TUESDAY", "WEDNESDAY"] and afterDays = ["MONDAY","FRIDAY"]
    // we should delete data for ["TUESDAY", "WEDNESDAY"] and add data for ["FRIDAY"]
    @Transactional
    public void modifyOperationDays(List<PartyRoomOperationDayDto> before, List<PartyRoomOperationDayDto> after) {
        List<DayOfWeek> beforeDays = before.stream().map(PartyRoomOperationDayDto::getOperationDay).collect(Collectors.toList());
        List<DayOfWeek> afterDays = after.stream().map(PartyRoomOperationDayDto::getOperationDay).collect(Collectors.toList());
        PartyRoomDto partyRoomDto = before.get(0).getPartyRoomDto();

        // Both on before and after -> Do nothing

        // Only on before -> Erase before data
        before.stream().filter(beforeDay -> !afterDays.contains(beforeDay.getOperationDay())).forEach(
                this::deleteOperationDay
        );

        // Only on after -> Add new data
        after.stream().filter(afterDay -> !beforeDays.contains(afterDay.getOperationDay())).forEach(
                afterDayDto -> {
                    afterDayDto.setPartyRoomDto(partyRoomDto);
                    this.registerOperationDay(afterDayDto);
                }
        );
    }
}
