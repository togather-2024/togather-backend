package com.togather.partyroom.core;

import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.core.model.PartyRoomDetailDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.register.PartyRoomRequestDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@Tag(name = "Party Room Core CRUD")
@RequestMapping("/api/partyroom")
public class PartyRoomController {
    private final PartyRoomService partyRoomService;
    private final MemberService memberService;
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_HOST')")
    @ResponseBody
    @Operation(summary = "partyRoom creation(Registration) API", description = "파티룸 등록 API")
    @ApiResponse(responseCode = "200", description = "success", content = @Content(schema = @Schema(implementation = PartyRoomDto.class)))
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "has no role or is not owner of party room", content = @Content)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "required in partyRoomDto: partyRoomName, desc, opening/closing hour, price, guestCapacity.<br>" +
                    "required in customTags: tagContents"
    )
    public ResponseEntity<PartyRoomDto> register(@Valid @RequestBody PartyRoomRequestDto partyRoomRequestDto) {

        PartyRoomDto partyRoomDto = partyRoomRequestDto.getPartyRoomDto();
        // DONE: extract from JWT token
        MemberDto partyRoomHost = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRequestDto.getPartyRoomImageDto();

        partyRoomDto.setPartyRoomHost(partyRoomHost);

        PartyRoomDto registeredPartyRoom = partyRoomService.register(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);

        return ResponseEntity.ok(registeredPartyRoom);
    }

    @PostMapping("/modify")
    @PreAuthorize("hasRole('ROLE_HOST')")
    @ResponseBody
    public ResponseEntity<PartyRoomDto> modify(@Valid @RequestBody PartyRoomRequestDto partyRoomRequestDto) {
        PartyRoomDto partyRoomDto = partyRoomService.findPartyRoomDtoById(partyRoomRequestDto.getPartyRoomDto().getPartyRoomId());

        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        // Compare user from token & user stored in DB. If different, the user is trying to modify a different user's party room
        if (loginUser.getMemberSrl() != partyRoomDto.getPartyRoomHost().getMemberSrl()) {
            throw new AccessDeniedException("cannot modify different user's party room");
        }

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRequestDto.getPartyRoomImageDto();

        PartyRoomDto registeredPartyRoom = partyRoomService.modifyPartyRoom(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);
        return ResponseEntity.ok(registeredPartyRoom);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable("id") long partyRoomId) {
        PartyRoomDto partyRoomDto = partyRoomService.findPartyRoomDtoById(partyRoomId);
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        // Compare user from token & user stored in DB. If different, the user is trying to delete a different user's party room
        if (loginUser.getMemberSrl() != partyRoomDto.getPartyRoomHost().getMemberSrl()) {
            throw new AccessDeniedException("cannot modify different user's party room");
        }
        partyRoomService.deletePartyRoomById(partyRoomId);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    @ResponseBody
    public ResponseEntity<PartyRoomDetailDto> getPartyRoomDetail(@PathVariable("id") long partyRoomId) {
        return ResponseEntity.ok(partyRoomService.findDetailDtoById(partyRoomId));
    }

}
