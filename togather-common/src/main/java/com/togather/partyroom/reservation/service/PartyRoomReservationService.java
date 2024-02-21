package com.togather.partyroom.reservation.service;

import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.repository.PartyRoomReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRoomReservationService {

    private final PartyRoomReservationRepository partyRoomReservationRepository;
    private final PartyRoomReservationConverter partyRoomReservationConverter;

    public void register(PartyRoomReservationDto partyRoomReservationDto) {
        PartyRoomReservation partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
        partyRoomReservationRepository.save(partyRoomReservation);

        log.info("save into party_room_reservation: {} ", partyRoomReservation.getReservationId());
    }

}
