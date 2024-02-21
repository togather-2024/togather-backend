package com.togather.partyroom.reservation.converter;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.repository.MemberRepository;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.repository.PartyRoomRepository;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomReservationConverter {

    private final PartyRoomConverter partyRoomConverter;
    private final PartyRoomRepository partyRoomRepository;
    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;

    public PartyRoomReservation convertToEntity(PartyRoomReservationDto partyRoomReservationDto) {
        PartyRoom partyRoom = partyRoomRepository.findById(partyRoomReservationDto.getReservationId())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 수정
        Member reservationGuest = memberRepository.findById(partyRoomReservationDto.getReservationGuestId())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 수정

        return PartyRoomReservation.builder()
                .reservationId(partyRoomReservationDto.getReservationId())
                .partyRoom(partyRoom)
                .reservationGuest(reservationGuest)
                .guestCount(partyRoomReservationDto.getGuestCount())
                .startTime(partyRoomReservationDto.getStartTime())
                .endTime(partyRoomReservationDto.getEndTime())
                .paymentStatus(partyRoomReservationDto.getPaymentStatus())
                .build();

    }

    public PartyRoomReservationDto convertToDto(PartyRoomReservation partyRoomReservation) {
        return PartyRoomReservationDto.builder()
                .reservationId(partyRoomReservation.getReservationId())
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomReservation.getPartyRoom()))
                .reservationGuest(memberConverter.convertToDto(partyRoomReservation.getReservationGuest()))
                .startTime(partyRoomReservation.getStartTime())
                .endTime(partyRoomReservation.getEndTime())
                .paymentStatus(partyRoomReservation.getPaymentStatus())
                .build();
    }
}
