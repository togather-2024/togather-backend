    package com.togather.partyroom.reservation;

    import com.togather.member.model.Member;
    import com.togather.member.service.MemberService;
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
            //TODO: paymentStatus 필드 처리

            partyRoomReservationService.register(reservationDto);

            return ResponseEntity.ok("ok");
        }

        @GetMapping("/my/{reservation-id}")
        public ResponseEntity<PartyRoomReservationDto> searchGuestReservation(@PathVariable(name = "reservation-id") long reservationId) {
            //특정 예약 내역 상세 조회
            //TODO: 토큰 검증 로직

            PartyRoomReservationDto findReservationDto = partyRoomReservationService.findOneByReservationId(reservationId);

            return ResponseEntity.ok(findReservationDto);

        }

        @GetMapping("/my")
        public ResponseEntity<List> searchGuestReservationList() {
            //guest - 전체 예약 내역 조회
            //TODO: 토큰 검증 로직 추가

            Member findMember = memberService.findById(1L); //TODO: 파라미터 변경
            List<PartyRoomReservationDto.Simple> findAllByGuest = partyRoomReservationService.findAllByGuest(findMember);

            return ResponseEntity.ok(findAllByGuest);
        }

        @GetMapping("/my/host")
        public ResponseEntity<List> searchHostReservationList() {
            //host - 등록한 파티룸에 대한 전체 예약 내역 조회
            //TODO: 토큰 검증 로직 추가(Role 검증?)

            Member findMember = memberService.findById(1L); //TODO: 파라미터 변경
            List<PartyRoomReservationDto.Simple> findAllByHost = partyRoomReservationService.findAllByHost(findMember);

            return ResponseEntity.ok(findAllByHost);
        }

        @DeleteMapping("/{reservation-id}")
        public ResponseEntity<String> delete(@PathVariable(name = "reservation-id") long reservationId) {

            //TODO: 토큰 검증 로직 추가
            partyRoomReservationService.delete(reservationId);

            return ResponseEntity.ok("ok");
        }

    }
