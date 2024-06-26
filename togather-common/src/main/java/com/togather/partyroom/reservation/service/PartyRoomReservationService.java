package com.togather.partyroom.reservation.service;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDetailDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomOperationDayService;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageType;
import com.togather.partyroom.image.service.PartyRoomImageService;
import com.togather.partyroom.location.service.PartyRoomLocationService;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentStatus;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationRequestDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationResponseDto;
import com.togather.partyroom.reservation.repository.PartyRoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PartyRoomReservationService {

    private final PartyRoomReservationRepository partyRoomReservationRepository;
    private final PartyRoomReservationConverter partyRoomReservationConverter;
    private final PartyRoomService partyRoomService;
    private final PartyRoomConverter partyRoomConverter;
    private final PartyRoomLocationService partyRoomLocationService;
    private final PartyRoomImageService partyRoomImageService;
    private final PartyRoomOperationDayService partyRoomOperationDayService;
    private final TaskScheduler taskScheduler;
    private final PartyRoomReservationScheduleService scheduleService;


    @Transactional
    public long register(PartyRoomReservationRequestDto partyRoomReservationRequestDto, MemberDto loginUser) {
        PartyRoomDto partyRoomDto = partyRoomService.findPartyRoomDtoById(partyRoomReservationRequestDto.getPartyRoomId());

        PartyRoomReservationDto partyRoomReservationDto = PartyRoomReservationDto.builder()
                .partyRoomDto(partyRoomDto)
                .reservationGuestDto(loginUser)
                .guestCount(partyRoomReservationRequestDto.getGuestCount())
                .startTime(partyRoomReservationRequestDto.getStartTime())
                .endTime(partyRoomReservationRequestDto.getEndTime())
                .paymentStatus(PaymentStatus.PENDING)
                .bookedDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .totalPrice(partyRoomReservationRequestDto.getTotalPrice())
                .build();

        isRoomReservationAvailable(partyRoomReservationDto);

        PartyRoomReservation partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
        partyRoomReservationRepository.save(partyRoomReservation);

        long reservationId = partyRoomReservation.getReservationId();

        log.info("save into party_room_reservation: {}", reservationId);

        taskScheduler.schedule(() -> scheduleService.removeExpiredReservation(reservationId), Instant.now().plusSeconds(30 * 60));

        return partyRoomReservation.getReservationId();
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
        List<PartyRoomReservation> partyRoomReservationList = partyRoomReservationRepository.findByDateTimeReservedAndPartyRoomId(partyRoomReservationDto.getStartTime(), partyRoomReservationDto.getEndTime(), partyRoomReservationDto.getPartyRoomDto().getPartyRoomId());

        for (PartyRoomReservation reservation : partyRoomReservationList)
            if (reservation.getPaymentStatus().equals(PaymentStatus.COMPLETE) || reservation.getPaymentStatus().equals(PaymentStatus.PENDING))
                throw new RuntimeException("already reserved");
    }

    private void isRoomReservationAvailable(PartyRoomReservationDto partyRoomReservationDto) {
        isValidTimeSlot(partyRoomReservationDto);
        isValidReservationCapacity(partyRoomReservationDto);
        isAlreadyReserved(partyRoomReservationDto);
    }

    public List<PartyRoomReservationResponseDto> findAllByMember(MemberDto memberDto) {

        List<PartyRoomReservation> findReservationListByGuest = partyRoomReservationRepository.findAllByGuest(memberDto.getMemberSrl());

        if (CollectionUtils.isEmpty(findReservationListByGuest)) {
            log.info("search party_room_reservation by reservation_id is empty");
            return Collections.emptyList();
        } else {
            log.info("search party_room_reservation list by memberSrl: {}",
                    findReservationListByGuest.get(0).getReservationGuest().getMemberSrl());

            return findReservationListByGuest.stream()
                    .map(r -> PartyRoomReservationResponseDto.builder()
                            .partyRoomImageDto(partyRoomImageService.findPartyRoomMainImageByPartyRoom(r.getPartyRoom()))
                            .partyRoomLocationDto(partyRoomLocationService.findLocationDtoByPartyRoom(r.getPartyRoom()))
                            .partyRoomReservationDto(partyRoomReservationConverter.convertToDto(r))
                            .build())
                    .toList();
        }
    }

    public PartyRoomReservationResponseDto findDtoByReservationId(long reservationId) {

        PartyRoomReservationDto partyRoomReservationDto = partyRoomReservationConverter.convertToDto(partyRoomReservationRepository.findById(reservationId)
                .orElseThrow(RuntimeException::new));

        String paymentKey = partyRoomReservationDto.getPaymentDto() == null ? null : partyRoomReservationDto.getPaymentDto().getPaymentKey();
        PartyRoomDetailDto partyRoomDetailDto = partyRoomService.findDetailDtoById(partyRoomReservationDto.getPartyRoomDto().getPartyRoomId());

        PartyRoomReservationResponseDto partyRoomReservationResponseDto = PartyRoomReservationResponseDto.builder()
                .partyRoomReservationDto(partyRoomReservationDto)
                .partyRoomLocationDto(partyRoomDetailDto.getPartyRoomLocationDto())
                .partyRoomImageDto(partyRoomDetailDto.getPartyRoomImageDtoList().stream()
                        .filter(image -> image.getPartyRoomImageType() == PartyRoomImageType.MAIN)
                        .findFirst().orElse(null))
                .paymentKey(paymentKey)
                .build();

        log.info("find party_room_reservation by reservation id: {}", reservationId);

        return partyRoomReservationResponseDto;
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

    public void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus) {
        PartyRoomReservation partyRoomReservation = payment.getPartyRoomReservation();
        partyRoomReservation.updatePaymentStatus(paymentStatus);
    }

    public PartyRoomReservationResponseDto.AvailableTimes searchAvailableTimes(long partyroomId, LocalDate date) {
        PartyRoom partyRoom = partyRoomService.findById(partyroomId);
        List<PartyRoomReservation> reservedList = partyRoomReservationRepository.findByPartyRoomAndDate(partyRoom, date);
        List<Integer> availableTimes = new ArrayList<>();

        List<Integer> reservedTimes = reservedList.stream()
                .filter(reservation -> reservation.getPaymentStatus() == PaymentStatus.COMPLETE || reservation.getPaymentStatus() == PaymentStatus.PENDING)
                .flatMap(reservation -> IntStream.range(reservation.getStartTime().getHour(), reservation.getEndTime().getHour()).boxed())
                .distinct()
                .toList();

        reservedTimes = reservedTimes.stream().distinct().toList();

        for (int i = partyRoom.getOpeningHour(); i < partyRoom.getClosingHour(); i++) {
            if (!reservedTimes.contains(i) &&
                    LocalDateTime.now(ZoneId.of("Asia/Seoul")).isBefore(LocalDateTime.of(date, LocalTime.of(i, 00))))
                availableTimes.add(i);
        }

        log.info("search available {}th party room reservation times", partyRoom.getPartyRoomId());

        return PartyRoomReservationResponseDto.AvailableTimes.builder()
                .availableTimes(availableTimes)
                .build();
    }

    public boolean hasFinishedPartyRoomReservation(PartyRoomReservationDto partyRoomReservationDto) {
        return LocalDateTime.now().isAfter(partyRoomReservationDto.getEndTime());
    }
}
