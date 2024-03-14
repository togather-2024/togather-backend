package com.togather.partyroom.reservation.service;

import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomOperationDayService;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.service.PartyRoomImageService;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.location.service.PartyRoomLocationService;
import com.togather.partyroom.payment.model.PaymentStatus;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final PartyRoomLocationService partyRoomLocationService;
    private final PartyRoomImageService partyRoomImageService;
    private final PartyRoomOperationDayService partyRoomOperationDayService;


    @Transactional
    public void register(PartyRoomReservationDto partyRoomReservationDto, MemberDto loginUser) {

        PartyRoom findPartyRoom = partyRoomService.findById(partyRoomReservationDto.getPartyRoomDto().getPartyRoomId());
        partyRoomReservationDto.setPartyRoomDto(partyRoomConverter.convertFromEntity(findPartyRoom));

        partyRoomReservationDto.setReservationGuestDto(loginUser);

        PartyRoomLocationDto partyRoomLocationDto = partyRoomLocationService.findLocationDtoByPartyRoom(findPartyRoom);
        partyRoomReservationDto.setPartyRoomLocationDto(partyRoomLocationDto);

        PartyRoomImageDto partyRoomImageDto = partyRoomImageService.findPartyRoomMainImageByPartyRoom(findPartyRoom); //main image
        partyRoomReservationDto.setPartyRoomImageDto(partyRoomImageDto);

        partyRoomReservationDto.setPaymentStatus(PaymentStatus.PENDING);
        partyRoomReservationDto.setBookedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        isRoomReservationAvailable(partyRoomReservationDto);

        PartyRoomReservation partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
        partyRoomReservationRepository.save(partyRoomReservation);

        log.info("save into party_room_reservation: {}", partyRoomReservation.getReservationId());
    }

    private void isValidReservationCapacity(PartyRoomReservationDto partyRoomReservationDto) {
        if (!(partyRoomReservationDto.getPartyRoomDto().getGuestCapacity() >= partyRoomReservationDto.getGuestCount()))
            throw new RuntimeException("exceeds capacity");
    }

    private void isValidTimeSlot(PartyRoomReservationDto partyRoomReservationDto) {
        List<PartyRoomOperationDayDto> findPartyRoomOperationDayList = partyRoomOperationDayService.findOperationDaysByPartyRoom(partyRoomConverter.convertFromDto(partyRoomReservationDto.getPartyRoomDto()));

        List<DayOfWeek> operationDays = findPartyRoomOperationDayList.stream()
                .map(PartyRoomOperationDayDto::getOperationDay)
                .toList();
        int openingHour = partyRoomReservationDto.getPartyRoomDto().getOpeningHour();
        int closingHour = partyRoomReservationDto.getPartyRoomDto().getClosingHour();

        DayOfWeek startDayOfWeek = partyRoomReservationDto.getStartTime().getDayOfWeek();
        DayOfWeek endDayOfWeek = partyRoomReservationDto.getEndTime().getDayOfWeek();
        int startHour = partyRoomReservationDto.getStartTime().getHour();
        int endHour = partyRoomReservationDto.getEndTime().getHour();

        if (!((operationDays.contains(startDayOfWeek) && operationDays.contains(endDayOfWeek))
                && (startHour >= openingHour && endHour <= closingHour)))
            throw new RuntimeException("unavailable for reservation");
    }

    private void isAlreadyReserved(PartyRoomReservationDto partyRoomReservationDto) {
        List<PartyRoomReservation> partyRoomReservationList = partyRoomReservationRepository.findByDateTimeReserved(partyRoomReservationDto.getStartTime(), partyRoomReservationDto.getEndTime());

        for (PartyRoomReservation reservation : partyRoomReservationList)
            if (reservation.getPaymentStatus().equals(PaymentStatus.COMPLETE) || reservation.getPaymentStatus().equals(PaymentStatus.PENDING))
                throw new RuntimeException("already reserved");
    }

    private void isRoomReservationAvailable(PartyRoomReservationDto partyRoomReservationDto) {
        isValidTimeSlot(partyRoomReservationDto);
        isValidReservationCapacity(partyRoomReservationDto);
        isAlreadyReserved(partyRoomReservationDto);
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

    @Transactional
    public void delete(PartyRoomReservation partyRoomReservation) {

        partyRoomReservationRepository.delete(partyRoomReservation);

        log.info("delete party_room_reservation: {}", partyRoomReservation.getReservationId());
    }

}
