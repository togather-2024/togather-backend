package com.togather.partyroom.image.service;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.image.converter.PartyRoomImageConverter;
import com.togather.partyroom.image.model.PartyRoomImage;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.model.PartyRoomImageType;
import com.togather.partyroom.image.repository.PartyRoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PartyRoomImageService {
    private final PartyRoomImageConverter partyRoomImageConverter;
    private final PartyRoomImageRepository partyRoomImageRepository;

    public PartyRoomImageDto findPartyRoomMainImageByPartyRoom(PartyRoom partyRoom) {
        return partyRoomImageConverter.convertFromEntity(partyRoomImageRepository.findByPartyRoomAndImageType(partyRoom, PartyRoomImageType.MAIN).get());
    }

    public void modifyPartyRoomImageFile(PartyRoomImageDto before, PartyRoomImageDto after) {
        PartyRoomImage beforeEntity = partyRoomImageConverter.convertFromDto(before);
        if (StringUtils.hasText(after.getImageFileName())) {
            beforeEntity.modifyImageFileName(after.getImageFileName());
        }
    }

    public void registerPartyRoomMainImage(PartyRoomImageDto partyRoomImageDto) {
        partyRoomImageDto.setToMainImage();
        partyRoomImageRepository.save(partyRoomImageConverter.convertFromDto(partyRoomImageDto));
    }
}
