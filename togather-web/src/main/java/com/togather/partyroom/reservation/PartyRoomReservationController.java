package com.togather.partyroom.reservation;

import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/partyroom/reservation")
@Slf4j
@RequiredArgsConstructor
public class PartyRoomReservationController {

    private final PartyRoomReservationService partyRoomReservationService;
    private final PartyRoomService partyRoomService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody PartyRoomReservationDto reservationDto) {

        //TODO: 토큰 검증 로직 추가

        //수용 인원을 넘지 않았는지 확인
        boolean isValidReservationCapacity = partyRoomService.isValidReservationCapacity(reservationDto.getPartyRoomId(), reservationDto.getGuestCount());
        //이용하려는 시간이 유효한지 확인
        boolean isValidTimeSlot = partyRoomService.isValidTimeSlot(reservationDto.getPartyRoomId(), reservationDto.getStartTime(), reservationDto.getEndTime());

        if (isValidReservationCapacity && isValidTimeSlot)
            partyRoomReservationService.register(reservationDto);
        else throw new RuntimeException(); //TODO: 예외 처리

        return ResponseEntity.ok("ok");
    }
}
