package com.togather.partyroom.image.service;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.image.converter.PartyRoomImageConverter;
import com.togather.partyroom.image.model.PartyRoomImage;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.model.PartyRoomImageType;
import com.togather.partyroom.image.repository.PartyRoomImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyRoomImageService {
    private final PartyRoomImageConverter partyRoomImageConverter;
    private final PartyRoomImageRepository partyRoomImageRepository;

    public PartyRoomImageDto findPartyRoomMainImageByPartyRoom(PartyRoom partyRoom) {
        return partyRoomImageConverter.convertFromEntity(partyRoomImageRepository.findByPartyRoomAndImageType(partyRoom, PartyRoomImageType.MAIN).orElse(null));
    }

    public List<PartyRoomImageDto> findAllImagesByPartyRoom(PartyRoom partyRoom) {
        return partyRoomImageRepository.findAllByPartyRoom(partyRoom).stream().map(partyRoomImageConverter::convertFromEntity).toList();
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

    @Transactional
    public void deleteAllByPartyRoom(PartyRoom partyRoom) {
        int deleteCount = partyRoomImageRepository.deleteAllByPartyRoom(partyRoom);
        log.info("[PartyRoomImageService - deleteAll] successfully deleted {} images for partyRoomId: {}, ", deleteCount, partyRoom.getPartyRoomId());
    }
}
