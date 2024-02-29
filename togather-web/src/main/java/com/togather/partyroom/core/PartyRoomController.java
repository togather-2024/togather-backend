package com.togather.partyroom.core;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.member.repository.MemberRepository;
import com.togather.partyroom.core.model.PartyRoomDetailDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.register.PartyRoomRequestDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/partyroom")
public class PartyRoomController {
    private final PartyRoomService partyRoomService;
    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<PartyRoomDto> register(@Valid @RequestBody PartyRoomRequestDto partyRoomRequestDto) {

        PartyRoomDto partyRoomDto = partyRoomRequestDto.getPartyRoomDto();
        // TODO: extract from JWT token
        MemberDto partyRoomHost = createMemberDtoForTest();

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRequestDto.getPartyRoomImageDto();

        partyRoomDto.setPartyRoomHost(partyRoomHost);

        PartyRoomDto registeredPartyRoom = partyRoomService.register(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);

        return ResponseEntity.ok(registeredPartyRoom);
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<PartyRoomDto> modify(@Valid @RequestBody PartyRoomRequestDto partyRoomRequestDto) {
        PartyRoomDto partyRoomDto = partyRoomService.findPartyRoomDtoById(partyRoomRequestDto.getPartyRoomDto().getPartyRoomId());
        // Compare JWT token and party room owner - only accept modification if requestClient == partyRoomOwner

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRequestDto.getPartyRoomImageDto();

        PartyRoomDto registeredPartyRoom = partyRoomService.modifyPartyRoom(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);
        return ResponseEntity.ok(registeredPartyRoom);
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> delete(@PathVariable("id") long partyRoomId) {
        // TODO: Check if owner of partyRoom
        partyRoomService.deletePartyRoomById(partyRoomId);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<PartyRoomDetailDto> getPartyRoomDetail(@PathVariable("id") long partyRoomId) {
        return ResponseEntity.ok(partyRoomService.findDetailDtoById(partyRoomId));
    }

    // TODO: MUST DELETE
    private MemberDto createMemberDtoForTest() {
        return memberConverter.convertToDto(memberRepository.findByEmail("hosttest@test.com").get());
    }
}
