package com.togather.partyroom.image.converter;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.image.model.PartyRoomImage;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomImageConverter {
    private final PartyRoomConverter partyRoomConverter;

    public PartyRoomImage convertFromDto(PartyRoomImageDto partyRoomImageDto) {
        if (partyRoomImageDto == null) {
            return null;
        }

        return PartyRoomImage.builder()
                .partyRoomImageId(partyRoomImageDto.getPartyRoomImageId())
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomImageDto.getPartyRoomDto()))
                .imageFileName(partyRoomImageDto.getImageFileName())
                .imageType(partyRoomImageDto.getPartyRoomImageType())
                .build();
    }
}
