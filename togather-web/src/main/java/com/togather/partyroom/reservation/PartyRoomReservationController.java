    package com.togather.partyroom.reservation;

    import com.togather.member.converter.MemberConverter;
    import com.togather.member.model.MemberDto;
    import com.togather.member.model.Role;
    import com.togather.member.repository.MemberRepository;
    import com.togather.member.service.MemberService;
    import com.togather.partyroom.core.converter.PartyRoomConverter;
    import com.togather.partyroom.core.model.PartyRoom;
    import com.togather.partyroom.core.service.PartyRoomService;
    import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
    import com.togather.partyroom.reservation.service.PartyRoomReservationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/partyroom/reservation")
    @Slf4j
    @RequiredArgsConstructor
    public class PartyRoomReservationController {

        private final PartyRoomReservationService partyRoomReservationService;
//        private final PartyRoomService partyRoomService;
//        private final PartyRoomConverter partyRoomConverter;
//        private final MemberService memberService;
//        private final MemberConverter memberConverter;

        @PostMapping("/register")
        public ResponseEntity<String> register(@Valid @RequestBody PartyRoomReservationDto reservationDto) {

            //TODO: 토큰 검증 로직 추가

//            PartyRoom findPartyRoom = partyRoomService.findById(reservationDto.getPartyRoomDto().getPartyRoomId());
//            MemberDto memberDto = memberConverter.convertToDto(memberService.findById(reservationDto.getReservationGuestDto().getMemberSrl()));
//            reservationDto.setReservationGuestDto(memberDto);
//            reservationDto.setPartyRoomDto(partyRoomConverter.convertFromEntity(findPartyRoom));
            partyRoomReservationService.register(reservationDto);

            return ResponseEntity.ok("ok");
        }

        @GetMapping("/search/{reservation-id}")
        public ResponseEntity<PartyRoomReservationDto> searchOneByReservationId(@PathVariable(name = "reservation-id") long reservationId) {

            //TODO: 토큰 검증 로직 추가

            PartyRoomReservationDto partyRoomReservationDto = partyRoomReservationService.findOneByReservationId(reservationId);

            return ResponseEntity.ok(partyRoomReservationDto);
        }

    }
