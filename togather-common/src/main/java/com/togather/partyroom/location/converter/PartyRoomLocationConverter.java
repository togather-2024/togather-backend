package com.togather.partyroom.location.converter;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.location.model.PartyRoomLocation;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomLocationConverter {
    private final PartyRoomConverter partyRoomConverter;

    public PartyRoomLocation convertFromDto(PartyRoomLocationDto partyRoomLocationDto) {
        if (partyRoomLocationDto == null) {
            return null;
        }

        return PartyRoomLocation.builder()
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomLocationDto.getPartyRoomDto()))
                .sido(partyRoomLocationDto.getSido())
                .sigungu(partyRoomLocationDto.getSigungu())
                .roadName(partyRoomLocationDto.getRoadName())
                .roadAddress(partyRoomLocationDto.getRoadAddress())
                .jibunAddress(partyRoomLocationDto.getJibunAddress())
                .build();
    }
}
