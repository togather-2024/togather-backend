package com.togather.partyroom.core.service;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.repository.PartyRoomOperationDayRepository;
import com.togather.partyroom.core.repository.PartyRoomRepository;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.service.PartyRoomImageService;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.location.service.PartyRoomLocationService;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import com.togather.partyroom.tags.service.PartyRoomCustomTagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyRoomService {
    private final PartyRoomConverter partyRoomConverter;
    private final PartyRoomRepository partyRoomRepository;
    private final PartyRoomCustomTagService partyRoomCustomTagService;
    private final PartyRoomOperationDayService partyRoomOperationDayService;
    private final PartyRoomLocationService partyRoomLocationService;
    private final PartyRoomImageService partyRoomImageService;
    private final PartyRoomOperationDayRepository partyRoomOperationDayRepository;

    @Transactional
    public PartyRoomDto register(PartyRoomDto partyRoomDto, List<PartyRoomCustomTagDto> customTags, PartyRoomLocationDto partyRoomLocationDto,
                                 PartyRoomImageDto mainPartyRoomImageDto, List<PartyRoomOperationDayDto> operationDayDtoList) {

        // Save party room basic data and persist to get PK of entity
        PartyRoom savedPartyRoomEntity = partyRoomRepository.save(partyRoomConverter.convertFromDto(partyRoomDto));
        partyRoomDto.setPartyRoomId(savedPartyRoomEntity.getPartyRoomId());

        // Save location data
        partyRoomLocationDto.setPartyRoomDto(partyRoomDto);
        partyRoomLocationService.registerLocation(partyRoomLocationDto);

        // Save operation day data
        operationDayDtoList.stream().forEach(operationDayDto -> operationDayDto.setPartyRoomDto(partyRoomDto));
        partyRoomOperationDayService.registerOperationDays(operationDayDtoList);

        // Save main party room image only when file exists
        if (StringUtils.hasText(mainPartyRoomImageDto.getImageFileName())) {
            mainPartyRoomImageDto.setPartyRoomDto(partyRoomDto);
            partyRoomImageService.registerPartyRoomMainImage(mainPartyRoomImageDto);
        }

        // Save tag related data
        partyRoomCustomTagService.registerTags(partyRoomDto, customTags);

        return partyRoomDto;
    }

    public PartyRoom findById(long partyRoomId) {
        return partyRoomRepository.findById(partyRoomId)
                .orElseThrow(RuntimeException::new);
    }

    public boolean isValidReservationCapacity(PartyRoomReservationDto partyRoomReservationDto) {
        return partyRoomReservationDto.getPartyRoomDto().getGuestCapacity() >= partyRoomReservationDto.getGuestCount();
    }

    public boolean isValidTimeSlot(PartyRoomReservationDto partyRoomReservationDto) {
        List<PartyRoomOperationDay> findPartyRoomOperationDay = partyRoomOperationDayRepository.findByPartyRoom(partyRoomConverter.convertFromDto(partyRoomReservationDto.getPartyRoomDto()));

        //TODO: 로직 구체화(중복 체크 등)
        List<DayOfWeek> operationDays = findPartyRoomOperationDay.stream()
                .map(PartyRoomOperationDay::getOperationDay)
                .toList();
        int openingHour = partyRoomReservationDto.getPartyRoomDto().getOpeningHour();
        int closingHour = partyRoomReservationDto.getPartyRoomDto().getClosingHour();

        DayOfWeek startDayOfWeek = partyRoomReservationDto.getStartTime().getDayOfWeek();
        DayOfWeek endDayOfWeek = partyRoomReservationDto.getEndTime().getDayOfWeek();
        int startHour = partyRoomReservationDto.getStartTime().getHour();
        int endHour = partyRoomReservationDto.getEndTime().getHour();

        return (operationDays.contains(startDayOfWeek) && operationDays.contains(endDayOfWeek))
                && (startHour >= openingHour && endHour <= closingHour);
    }
}
