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

        PartyRoomDto partyRoomDto = partyRoomRegisterRequestDto.extractPartyRoomDto();
        // TODO: extract from JWT token
        MemberDto partyRoomHost = createMemberDtoForTest();

        PartyRoomLocationDto partyRoomLocationDto = partyRoomRegisterRequestDto.extractLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRegisterRequestDto.extractCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRegisterRequestDto.extractOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRegisterRequestDto.extractMainImageDto();

        partyRoomDto.setPartyRoomHost(partyRoomHost);

        PartyRoomDto registeredPartyRoom = partyRoomService.register(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);

        return ResponseEntity.ok(null);
    }

    // TODO: MUST DELETE
    private MemberDto createMemberDtoForTest() {
        return memberConverter.convertToDto(memberRepository.findByEmail("hosttest@test.com").get());
    }
}
