    package com.togather.partyroom.reservation;

    import com.togather.common.AddJsonFilters;
    import com.togather.member.model.MemberDto;
    import com.togather.member.service.MemberService;
    import com.togather.partyroom.reservation.model.PartyRoomReservation;
    import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
    import com.togather.partyroom.reservation.service.PartyRoomReservationService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.http.ResponseEntity;
    import org.springframework.http.converter.json.MappingJacksonValue;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    import static com.togather.common.ResponseFilter.*;

    @RestController
    @RequestMapping("/partyroom/reservation")
    @Slf4j
    @RequiredArgsConstructor
    @Tag(name = "Party Room Reservation CRUD")
    public class PartyRoomReservationController {

        private final PartyRoomReservationService partyRoomReservationService;
        private final MemberService memberService;

        @PostMapping("/registration")
        @PreAuthorize("hasRole('ROLE_GUEST')")
        @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
        @Operation(summary = "Party Room Reservation Registration", description = "파티룸 예약 등록 API")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "<h3>Required Data List</h3>" +
                        "partyRoomDto: partyRoomId<br>" +
                        "guestCount, startTime, endTime, totalPrice"
        )
        public ResponseEntity<String> register(@Valid @RequestBody PartyRoomReservationDto reservationDto) {
            MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
            partyRoomReservationService.register(reservationDto, loginUser);

            return ResponseEntity.ok("ok");
        }

        @GetMapping("/my/{reservation-id}")
        @PreAuthorize("hasRole('ROLE_GUEST')")
        @Operation(summary = "Party Room Reservation Search - One", description = "파티룸 예약 상세 조회 API")
        @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
        @AddJsonFilters(filters = {PARTY_ROOM_RESERVATION_DTO, PARTY_ROOM_DTO_SIMPLE, MEMBER_DTO_FOR_RESERVATION})
        @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReservationDto.class)))
        public MappingJacksonValue searchOneByReservationIdFiltered(@PathVariable(name = "reservation-id") long reservationId) {
            //guest - 특정 예약 내역 상세 조회

            PartyRoomReservationDto findReservationDto = partyRoomReservationService.findDtoByReservationId(reservationId);

            MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
            if (findReservationDto.getReservationGuestDto().getMemberSrl() != loginUser.getMemberSrl())
                throw new AccessDeniedException("not the same member");

            return new MappingJacksonValue(findReservationDto);
        }

        @GetMapping("/my")
        @PreAuthorize("hasRole('ROLE_GUEST')")
        @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
        @Operation(summary = "Party Room Reservation Search - List", description = "파티룸 예약 리스트 조회 API")
        @AddJsonFilters(filters = {PARTY_ROOM_DTO_SIMPLE, PARTY_ROOM_RESERVATION_DTO_SIMPLE,
                PARTY_ROOM_IMAGE_DTO_SIMPLE, MEMBER_DTO_FOR_RESERVATION})
        @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReservationDto.class)))
        public MappingJacksonValue searchByMember() {
            //guest - 전체 예약 내역 조회

            MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
            List<PartyRoomReservationDto> findAllByMember = partyRoomReservationService.findAllByMember(loginUser);

            return new MappingJacksonValue(findAllByMember);
        }

        @DeleteMapping("/{reservation-id}")
        @PreAuthorize("hasRole('ROLE_GUEST')")
        @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
        @Operation(summary = "Party Room Reservation Delete", description = "파티룸 예약 삭제 API")
        public ResponseEntity<String> delete(@PathVariable(name = "reservation-id") long reservationId) {
            MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
            PartyRoomReservation findPartyRoomReservation = partyRoomReservationService.findByReservationId(reservationId);

            if (findPartyRoomReservation.getReservationGuest().getMemberSrl() != loginUser.getMemberSrl())
                throw new AccessDeniedException("not the same member");

            partyRoomReservationService.delete(findPartyRoomReservation);

            return ResponseEntity.ok("ok");
        }

    }
