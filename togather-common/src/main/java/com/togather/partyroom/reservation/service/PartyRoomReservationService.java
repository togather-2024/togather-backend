package com.togather.partyroom.reservation.service;

import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import com.togather.partyroom.core.repository.PartyRoomOperationDayRepository;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.repository.PartyRoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRoomReservationService {

    private final PartyRoomReservationRepository partyRoomReservationRepository;
    private final PartyRoomReservationConverter partyRoomReservationConverter;
    private final PartyRoomService partyRoomService;
    private final PartyRoomConverter partyRoomConverter;
    private final MemberService memberService;
    private final PartyRoomOperationDayRepository partyRoomOperationDayRepository;


    @Transactional
    public void register(PartyRoomReservationDto partyRoomReservationDto) {

        PartyRoom findPartyRoom = partyRoomService.findById(partyRoomReservationDto.getPartyRoomDto().getPartyRoomId());
        MemberDto memberDto = memberService.findMemberDtoById(partyRoomReservationDto.getReservationGuestDto().getMemberSrl());
        partyRoomReservationDto.setReservationGuestDto(memberDto);
        partyRoomReservationDto.setPartyRoomDto(partyRoomConverter.convertFromEntity(findPartyRoom));


        boolean isValidReservationCapacity = isValidReservationCapacity(partyRoomReservationDto);
        boolean isValidTimeSlot = isValidTimeSlot(partyRoomReservationDto);

        if (isValidReservationCapacity && isValidTimeSlot) {
            PartyRoomReservation partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
            partyRoomReservationRepository.save(partyRoomReservation);

            log.info("save into party_room_reservation: {} ", partyRoomReservation.getReservationId());
        } else throw new RuntimeException(); //TODO: 예외 처리
    }

    public List<PartyRoomReservationDto> findAllByMember(MemberDto memberDto) {

        List<PartyRoomReservation> findAllByGuest = partyRoomReservationRepository.findAllByGuest(memberDto.getMemberSrl());

        if (CollectionUtils.isEmpty(findAllByGuest)) {
            log.info("search party_room_reservation by reservation_id is empty");
            return Collections.emptyList();
        } else {
            log.info("search party_room_reservation by reservation_id: {}", findAllByGuest.get(0).getReservationId());

            return findAllByGuest.stream()
                    .map(partyRoomReservationConverter::convertToDto)
                    .collect(Collectors.toList());
        }
    }

    public PartyRoomReservationDto findDtoByReservationId(long reservationId) {
        PartyRoomReservationDto findPartyRoomReservationDto = partyRoomReservationConverter.convertToDto(
                partyRoomReservationRepository.findById(reservationId).orElseThrow(RuntimeException::new));

        log.info("find party_room_reservation by reservation id: {}", reservationId);

        return findPartyRoomReservationDto;
    }

    public PartyRoomReservation findByReservationId(long reservationId) {

        PartyRoomReservation findPartyRoomReservation = partyRoomReservationRepository.findById(reservationId)
                .orElseThrow(RuntimeException::new);

        log.info("find party_room_reservation by reservation id: {}", reservationId);

        return findPartyRoomReservation;
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

    @Transactional
    public void delete(PartyRoomReservation partyRoomReservation) {

        partyRoomReservationRepository.delete(partyRoomReservation);

        log.info("delete party_room_reservation: {}", partyRoomReservation.getReservationId());
    }

}
