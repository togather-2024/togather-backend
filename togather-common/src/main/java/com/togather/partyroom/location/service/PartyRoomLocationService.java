package com.togather.partyroom.location.service;

import com.togather.partyroom.location.converter.PartyRoomLocationConverter;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.location.repository.PartyRoomLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyRoomLocationService {
    private final PartyRoomLocationRepository partyRoomLocationRepository;
    private final PartyRoomLocationConverter partyRoomLocationConverter;

    public void registerLocation(PartyRoomLocationDto partyRoomLocationDto) {
        partyRoomLocationRepository.save(partyRoomLocationConverter.convertFromDto(partyRoomLocationDto));
    }
}
