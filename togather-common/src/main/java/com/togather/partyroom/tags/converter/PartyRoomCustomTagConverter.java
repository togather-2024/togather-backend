package com.togather.partyroom.tags.converter;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRel;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRelDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomCustomTagConverter {
    private final PartyRoomConverter partyRoomConverter;

    public PartyRoomCustomTagDto convertFromEntity(PartyRoomCustomTag partyRoomCustomTag) {
        if (partyRoomCustomTag == null) {
            return null;
        }
        return PartyRoomCustomTagDto.builder()
                .tagContent(partyRoomCustomTag.getTagContent())
                .tagCount(partyRoomCustomTag.getTagCount())
                .build();
    }

    public PartyRoomCustomTag convertFromDto(PartyRoomCustomTagDto partyRoomCustomTagDto) {
        if (partyRoomCustomTagDto == null) {
            return null;
        }
        return PartyRoomCustomTag.builder()
                .tagContent(partyRoomCustomTagDto.getTagContent())
                .tagCount(partyRoomCustomTagDto.getTagCount())
                .build();
    }

    public PartyRoomCustomTagRel convertFromDto(PartyRoomCustomTagRelDto partyRoomCustomTagRelDto) {
        if (partyRoomCustomTagRelDto == null) {
            return null;
        }

        return PartyRoomCustomTagRel.builder()
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomCustomTagRelDto.getPartyRoomDto()))
                .partyRoomCustomTag(convertFromDto(partyRoomCustomTagRelDto.getPartyRoomCustomTagDto()))
                .build();
    }

    public PartyRoomCustomTagRel convertFromDto(PartyRoomDto partyRoomDto, PartyRoomCustomTagDto partyRoomCustomTagDto) {
        PartyRoomCustomTagRelDto dto = PartyRoomCustomTagRelDto.builder()
                .partyRoomDto(partyRoomDto)
                .partyRoomCustomTagDto(partyRoomCustomTagDto)
                .build();

        return convertFromDto(dto);
    }
}
