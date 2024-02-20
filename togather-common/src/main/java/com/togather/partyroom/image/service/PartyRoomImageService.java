package com.togather.partyroom.image.service;

import com.togather.partyroom.image.converter.PartyRoomImageConverter;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.repository.PartyRoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyRoomImageService {
    private final PartyRoomImageConverter partyRoomImageConverter;
    private final PartyRoomImageRepository partyRoomImageRepository;

    public void registerPartyRoomMainImage(PartyRoomImageDto partyRoomImageDto) {
        partyRoomImageDto.setToMainImage();
        partyRoomImageRepository.save(partyRoomImageConverter.convertFomDto(partyRoomImageDto));
    }
}
