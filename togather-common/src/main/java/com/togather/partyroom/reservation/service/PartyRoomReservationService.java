package com.togather.partyroom.reservation.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.service.PartyRoomService;
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
    private final PartyRoomService partyRoomService;
    private final MemberConverter memberConverter;
    private final PartyRoomConverter partyRoomConverter;
    private final MemberService memberService;


    @Transactional
    public void register(PartyRoomReservationDto partyRoomReservationDto) {

        PartyRoom findPartyRoom = partyRoomService.findById(partyRoomReservationDto.getPartyRoomDto().getPartyRoomId());
        MemberDto memberDto = memberConverter.convertToDto(memberService.findById(partyRoomReservationDto.getReservationGuestDto().getMemberSrl()));
        partyRoomReservationDto.setReservationGuestDto(memberDto);
        partyRoomReservationDto.setPartyRoomDto(partyRoomConverter.convertFromEntity(findPartyRoom));


        boolean isValidReservationCapacity = partyRoomService.isValidReservationCapacity(partyRoomReservationDto);
        boolean isValidTimeSlot = partyRoomService.isValidTimeSlot(partyRoomReservationDto);
        PartyRoomReservation partyRoomReservation;

        if (isValidReservationCapacity && isValidTimeSlot) {
            partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
            partyRoomReservationRepository.save(partyRoomReservation);
        } else throw new RuntimeException(); //TODO: 예외 처리

        log.info("save into party_room_reservation: {} ", partyRoomReservation.getReservationId());

    }

    public PartyRoomReservationDto findOneByReservationId(long reservationId) {

        PartyRoomReservation findPartyRoomReservation = partyRoomReservationRepository.findById(reservationId)
                .orElseThrow(RuntimeException::new);

        log.info("search party_room_reservation by reservation_id: {}", reservationId);

        return partyRoomReservationConverter.convertToDto(findPartyRoomReservation);
    }

}
