package com.togather.partyroom.core;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.MemberDto;
import com.togather.member.repository.MemberRepository;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.register.PartyRoomRegisterRequestDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public ResponseEntity<PartyRoomDto> register(@Valid @RequestBody PartyRoomRegisterRequestDto partyRoomRegisterRequestDto) {

        PartyRoomDto partyRoomDto = partyRoomRegisterRequestDto.getPartyRoomDto();
        // TODO: extract from JWT token
        MemberDto partyRoomHost = createMemberDtoForTest();

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRegisterRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRegisterRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRegisterRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRegisterRequestDto.getPartyRoomImageDto();

        partyRoomDto.setPartyRoomHost(partyRoomHost);

        PartyRoomDto registeredPartyRoom = partyRoomService.register(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/modify")
    @ResponseBody
    public ResponseEntity<PartyRoomDto> modify(@Valid @RequestBody PartyRoomRegisterRequestDto partyRoomRegisterRequestDto) {
        PartyRoomDto partyRoomDto = partyRoomService.findPartyRoomById(partyRoomRegisterRequestDto.getPartyRoomDto().getPartyRoomId());
        // Compare JWT token and party room owner - only accept modification if requestClient == partyRoomOwner

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRegisterRequestDto.getPartyRoomLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRegisterRequestDto.getCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRegisterRequestDto.getOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRegisterRequestDto.getPartyRoomImageDto();

        PartyRoomDto registeredPartyRoom = partyRoomService.modifyPartyRoom(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);
        return ResponseEntity.ok(registeredPartyRoom);
    }

    // TODO: MUST DELETE
    private MemberDto createMemberDtoForTest() {
        return memberConverter.convertToDto(memberRepository.findByEmail("hosttest@test.com").get());
    }
}
