package com.togather.partyroom.reservation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.togather.member.model.Member;
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

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;

import java.util.List;

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
    private final ObjectMapper objectMapper;

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

    public List<PartyRoomReservationDto.Simple> findAllByGuest(Member guest) {

        List<PartyRoomReservation> findAllByGuest = partyRoomReservationRepository.findAllByGuest(guest);

        if (findAllByGuest.isEmpty()) {
            log.info("party_room_reservation is empty: {}", findAllByGuest.get(0).getReservationId());
            return null;
        } else {
            //TODO: 삭제된 파티룸에 대한 예외 처리
            log.info("search party_room_reservation_list by member: {}", guest.getMemberSrl());
            return findAllByGuest.stream()
                    .map(reservation -> PartyRoomReservationDto.Simple.builder()
                            .reservationId(reservation.getReservationId())
                            .partyRoomName(reservation.getPartyRoom().getPartyRoomName())
                            .guestCount(reservation.getGuestCount())
                            .startTime(reservation.getStartTime())
                            .endTime(reservation.getEndTime())
                            .paymentStatus(reservation.getPaymentStatus())
                            .build())
                    .toList();
        }
    }

    public String findOneByReservationId(long reservationId) {

        PartyRoomReservationDto findPartyRoomReservationDto = partyRoomReservationConverter.convertToDto(
                partyRoomReservationRepository.findById(reservationId).orElseThrow(RuntimeException::new));
        System.out.println(findPartyRoomReservationDto.getReservationId());

        try {
            ObjectMapper filteredObjectMapper = new ObjectMapper();
            filteredObjectMapper
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .registerModule(new JavaTimeModule())
                    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter("PartyRoomReservationDtoFilter", SimpleBeanPropertyFilter.serializeAll())
                    .addFilter("PartyRoomDtoFilter", SimpleBeanPropertyFilter.serializeAll())
                    .addFilter("MemberDtoFilter", SimpleBeanPropertyFilter.filterOutAllExcept("memberName", "email", "profilePicFile"));

            filteredObjectMapper.setFilterProvider(filterProvider);

            return filteredObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(findPartyRoomReservationDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parsing error", e);
        }
    }

    public List<PartyRoomReservationDto.Simple> findAllByHost(Member host) {

        List<PartyRoomReservation> findAllByHost = partyRoomReservationRepository.findAllByHost(host);

        if (findAllByHost.isEmpty()) {
            log.info("party_room_reservation is empty: {}", findAllByHost.get(0).getReservationId());
            return null;
        } else {
            log.info("search party_room_reservation_list by member: {}", host.getMemberSrl());
            return findAllByHost.stream()
                    .map(reservation -> PartyRoomReservationDto.Simple.builder()
                            .reservationId(reservation.getReservationId())
                            .partyRoomName(reservation.getPartyRoom().getPartyRoomName())
                            .guestCount(reservation.getGuestCount())
                            .startTime(reservation.getStartTime())
                            .endTime(reservation.getEndTime())
                            .paymentStatus(reservation.getPaymentStatus())
                            .reservationGuestName(reservation.getReservationGuest().getMemberName())
                            .build())
                    .toList();
        }
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
    public void delete(long reservationId) {
        PartyRoomReservation findPartyRoomReservation = partyRoomReservationRepository.findById(reservationId)
                .orElseThrow(RuntimeException::new);

        partyRoomReservationRepository.delete(findPartyRoomReservation);

        log.info("delete party_room_reservation: {}", reservationId);
    }

}
