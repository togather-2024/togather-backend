    package com.togather.partyroom.reservation;

    import com.togather.common.ModifyResponse;
    import com.togather.member.model.Member;
    import com.togather.member.service.MemberService;
    import com.togather.partyroom.core.model.FieldFilteringPolicy;
    import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
    import com.togather.partyroom.reservation.service.PartyRoomReservationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/partyroom/reservation")
    @Slf4j
    @RequiredArgsConstructor
    public class PartyRoomReservationController {

        private final PartyRoomReservationService partyRoomReservationService;
        private final MemberService memberService;

        @PostMapping("/registration")
        public ResponseEntity<String> register(@Valid @RequestBody PartyRoomReservationDto reservationDto) {

            //TODO: 토큰 검증 로직 추가

            partyRoomReservationService.register(reservationDto);

            return ResponseEntity.ok("ok");
        }

        @GetMapping("/my/{reservation-id}")
        @ModifyResponse(policy = FieldFilteringPolicy.PARTY_ROOM_RESERVATION_DTO_PII_DATA_TEST)
        public ResponseEntity<PartyRoomReservationDto> searchOneByReservationId(@PathVariable(name = "reservation-id") long reservationId) {
            //guest - 특정 예약 내역 상세 조회
            //TODO: 토큰 검증 로직, 토큰에서 회원 정보 빼오기

            PartyRoomReservationDto findReservationDto = partyRoomReservationService.findOneByReservationId(reservationId);

            return ResponseEntity.ok(findReservationDto);

        }

        @GetMapping("/my")
        @ModifyResponse(policy = FieldFilteringPolicy.PARTY_ROOM_RESERVATION_DTO_PII_DATA_TEST)
        public ResponseEntity<List> searchByMember() {
            //guest - 전체 예약 내역 조회
            //TODO: 토큰 검증 로직 추가

            Member findMember = memberService.findById(1L); //TODO: 파라미터 변경
            List<PartyRoomReservationDto.Simple> findAllByMember = partyRoomReservationService.findAllByMember(findMember);

//            PartyRoomReservationDto findReservationDto1 = partyRoomReservationService.findOneByReservationId(2);
//            PartyRoomReservationDto findReservationDto2 = partyRoomReservationService.findOneByReservationId(3);
//            PartyRoomReservationDto findReservationDto3 = partyRoomReservationService.findOneByReservationId(4);
//            List<PartyRoomReservationDto> findAllByMember = List.of(findReservationDto3, findReservationDto2, findReservationDto1);

            return ResponseEntity.ok(findAllByMember);
        }

        @DeleteMapping("/{reservation-id}")
        public ResponseEntity<String> delete(@PathVariable(name = "reservation-id") long reservationId) {

            //TODO: 토큰 검증 로직 추가
            partyRoomReservationService.delete(reservationId);

            return ResponseEntity.ok("ok");
        }

    }
