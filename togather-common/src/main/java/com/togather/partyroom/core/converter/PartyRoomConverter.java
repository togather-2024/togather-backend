package com.togather.partyroom.core.converter;

import com.togather.member.converter.MemberConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomConverter {
    private final MemberConverter memberConverter;

    public PartyRoomDto convertFromEntity(PartyRoom partyRoom) {
        if (partyRoom == null) {
            return null;
        }
        return PartyRoomDto.builder()
                .partyRoomName(partyRoom.getPartyRoomName())
                .partyRoomHost(memberConverter.convertToDto(partyRoom.getPartyRoomHost()))
                .partyRoomDesc(partyRoom.getPartyRoomDesc())
                .openingHour(partyRoom.getOpeningHour())
                .closingHour(partyRoom.getClosingHour())
                .price(partyRoom.getPrice())
                .guestCapacity(partyRoom.getGuestCapacity())
                .partyRoomViewCount(partyRoom.getPartyRoomViewCount())
                .build();
    }

    public PartyRoom convertFromDto(PartyRoomDto partyRoomDto) {
        if (partyRoomDto == null) {
            return null;
        }

        return PartyRoom.builder()
                .partyRoomName(partyRoomDto.getPartyRoomName())
                .partyRoomHost(memberConverter.convertToEntity(partyRoomDto.getPartyRoomHost()))
                .partyRoomDesc(partyRoomDto.getPartyRoomDesc())
                .openingHour(partyRoomDto.getOpeningHour())
                .closingHour(partyRoomDto.getClosingHour())
                .price(partyRoomDto.getPrice())
                .guestCapacity(partyRoomDto.getGuestCapacity())
                .partyRoomViewCount(partyRoomDto.getPartyRoomViewCount())
                .build();
    }
}
