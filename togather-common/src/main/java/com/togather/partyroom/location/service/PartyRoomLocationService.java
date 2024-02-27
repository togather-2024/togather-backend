package com.togather.partyroom.location.service;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.location.converter.PartyRoomLocationConverter;
import com.togather.partyroom.location.model.PartyRoomLocation;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.location.repository.PartyRoomLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyRoomLocationService {
    private final PartyRoomLocationRepository partyRoomLocationRepository;
    private final PartyRoomLocationConverter partyRoomLocationConverter;

    public PartyRoomLocationDto findByPartyRoom(PartyRoom partyRoom) {
        return partyRoomLocationConverter.convertFromEntity(partyRoomLocationRepository.findByPartyRoom(partyRoom).get());
    }

    public void modifyPartyRoomLocation(PartyRoomLocationDto before, PartyRoomLocationDto after) {
        PartyRoomLocation beforeEntity = partyRoomLocationConverter.convertFromDto(before);
        beforeEntity.modifyPartyRoomLocation(partyRoomLocationConverter.convertFromDto(after));
    }

    public void registerLocation(PartyRoomLocationDto partyRoomLocationDto) {
        partyRoomLocationRepository.save(partyRoomLocationConverter.convertFromDto(partyRoomLocationDto));
    }
}
