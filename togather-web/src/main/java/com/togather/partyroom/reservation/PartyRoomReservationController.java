package com.togather.partyroom.reservation;

import com.togather.common.response.AddJsonFilters;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationRequestDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationResponseDto;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.togather.common.response.ResponseFilter.*;

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
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @Operation(summary = "Party Room Reservation Registration", description = "파티룸 예약 등록 API")
    public ResponseEntity<Long> register(@Valid @RequestBody PartyRoomReservationRequestDto reservationRequestDto) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        long reservationId = partyRoomReservationService.register(reservationRequestDto, loginUser);

        return ResponseEntity.ok(reservationId);
    }

    @GetMapping("/my/{reservation-id}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @Operation(summary = "Party Room Reservation Search - One", description = "파티룸 예약 상세 조회 API")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @AddJsonFilters(filters = {PARTY_ROOM_DTO_SIMPLE, MEMBER_DTO_FOR_RESERVATION, PARTY_ROOM_IMAGE_DTO_SIMPLE})
    @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReservationDto.class)))
    public MappingJacksonValue searchOneByReservationIdFiltered(@PathVariable(name = "reservation-id") long reservationId) {
        //guest - 특정 예약 내역 상세 조회

        PartyRoomReservationResponseDto reservationResponseDto = partyRoomReservationService.findDtoByReservationId(reservationId);

        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        if (reservationResponseDto.getPartyRoomReservationDto().getReservationGuestDto().getMemberSrl() != loginUser.getMemberSrl())
            throw new AccessDeniedException("not the same member");

        return new MappingJacksonValue(reservationResponseDto);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @Operation(summary = "Party Room Reservation Search - List", description = "파티룸 예약 리스트 조회 API")
    @AddJsonFilters(filters = {PARTY_ROOM_DTO_SIMPLE, PARTY_ROOM_RESERVATION_RESPONSE_DTO_SIMPLE,
            PARTY_ROOM_IMAGE_DTO_SIMPLE, MEMBER_DTO_FOR_RESERVATION})
    @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReservationDto.class)))
    public MappingJacksonValue searchByMember() {
        //guest - 전체 예약 내역 조회

        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        List<PartyRoomReservationResponseDto> findReservationResponseDtos = partyRoomReservationService.findAllByMember(loginUser);

        return new MappingJacksonValue(findReservationResponseDtos);
    }

    @DeleteMapping("/{reservation-id}")
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @Operation(summary = "Party Room Reservation Delete", description = "파티룸 예약 삭제 API")
    public ResponseEntity<String> delete(@PathVariable(name = "reservation-id") long reservationId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        PartyRoomReservation findPartyRoomReservation = partyRoomReservationService.findByReservationId(reservationId);

        if (findPartyRoomReservation.getReservationGuest().getMemberSrl() != loginUser.getMemberSrl())
            throw new AccessDeniedException("not the same member");

        partyRoomReservationService.delete(findPartyRoomReservation);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/search/available")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = PartyRoomReservationResponseDto.class)))
    @Operation(summary = "Search Available Party Room Reservation Times", description = "예약 가능한 파티룸 시간대 조회")
    public ResponseEntity<PartyRoomReservationResponseDto.AvailableTimes> searchAvailableReservationTimes(@RequestParam long partyroomId,
                                                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        PartyRoomReservationResponseDto.AvailableTimes availableTimes = partyRoomReservationService.searchAvailableTimes(partyroomId, date);

        return ResponseEntity.ok(availableTimes);
    }
}
