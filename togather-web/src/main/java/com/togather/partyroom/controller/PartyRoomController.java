package com.togather.partyroom.controller;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.service.PartyRoomService;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.register.PartyRoomRegisterRequestDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/partyroom")
public class PartyRoomController {
    private final PartyRoomService partyRoomService;
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<PartyRoomDto> register(PartyRoomRegisterRequestDto partyRoomRegisterRequestDto) {

        PartyRoomDto partyRoomDto = partyRoomRegisterRequestDto.extractPartyRoomDto();
        // TODO: extract from JWT token
        MemberDto partyRoomHost = null;
        PartyRoomLocationDto partyRoomLocationDto = partyRoomRegisterRequestDto.extractLocationDto();
        List<PartyRoomCustomTagDto> customTags = partyRoomRegisterRequestDto.extractCustomTags();
        List<PartyRoomOperationDayDto> operationDayDtos = partyRoomRegisterRequestDto.extractOperationDays();
        PartyRoomImageDto partyRoomMainImageDto = partyRoomRegisterRequestDto.extractMainImageDto();

        partyRoomDto.setPartyRoomHost(partyRoomHost);
        partyRoomLocationDto.setPartyRoomDto(partyRoomDto);
        operationDayDtos.stream().forEach(operationDayDto -> operationDayDto.setPartyRoomDto(partyRoomDto));
        partyRoomMainImageDto.setPartyRoomDto(partyRoomDto);


        PartyRoomDto registeredPartyRoom = partyRoomService.register(partyRoomDto, customTags, partyRoomLocationDto, partyRoomMainImageDto, operationDayDtos);
        return ResponseEntity.ok(registeredPartyRoom);
    }

    // TODO: MUST DELETE
    private MemberDto createMemberDtoForTest() {
        return null;
    }
}
