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
                .partyRoomLocationId(partyRoomLocationDto.getPartyRoomLocationId())
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomLocationDto.getPartyRoomDto()))
                .sido(partyRoomLocationDto.getSido())
                .sigungu(partyRoomLocationDto.getSigungu())
                .roadName(partyRoomLocationDto.getRoadName())
                .roadAddress(partyRoomLocationDto.getRoadAddress())
                .jibunAddress(partyRoomLocationDto.getJibunAddress())
                .detailAddress(partyRoomLocationDto.getDetailAddress())
                .build();
    }

    public PartyRoomLocationDto convertFromEntity(PartyRoomLocation partyRoomLocation) {
        if (partyRoomLocation == null) {
            return null;
        }

        return PartyRoomLocationDto.builder()
                .partyRoomLocationId(partyRoomLocation.getPartyRoomLocationId())
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomLocation.getPartyRoom()))
                .sido(partyRoomLocation.getSido())
                .sigungu(partyRoomLocation.getSigungu())
                .roadName(partyRoomLocation.getRoadName())
                .roadAddress(partyRoomLocation.getRoadAddress())
                .jibunAddress(partyRoomLocation.getJibunAddress())
                .detailAddress(partyRoomLocation.getDetailAddress())
                .build();
    }
}
