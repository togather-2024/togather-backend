    package com.togather.partyroom.reservation;

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

        @PostMapping("/registration")
        public ResponseEntity<String> register(@Valid @RequestBody PartyRoomReservationDto reservationDto) {

            //TODO: 토큰 검증 로직 추가

            partyRoomReservationService.register(reservationDto);

            return ResponseEntity.ok("ok");
        }

        @GetMapping("/{reservation-id}")
        public ResponseEntity<PartyRoomReservationDto> searchOneByReservationId(@PathVariable(name = "reservation-id") long reservationId) {

            //TODO: 토큰 검증 로직 추가

            PartyRoomReservationDto partyRoomReservationDto = partyRoomReservationService.findOneByReservationId(reservationId);

            return ResponseEntity.ok(partyRoomReservationDto);
        }

        @GetMapping("/{reservation-id}")

        @DeleteMapping("/{reservation-id}")
        public ResponseEntity<String> delete(@PathVariable(name = "reservation-id") long reservationId) {

            //TODO: 토큰 검증 로직 추가
            partyRoomReservationService.delete(reservationId);

            return ResponseEntity.ok("ok");
        }

    }
