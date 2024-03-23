package com.togather.partyroom.image.service;

import com.togather.common.s3.S3ImageUploader;
import com.togather.common.s3.S3ObjectDto;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartyRoomImageService {
    private final PartyRoomImageConverter partyRoomImageConverter;
    private final PartyRoomImageRepository partyRoomImageRepository;
    private final S3ImageUploader s3ImageUploader;

    public PartyRoomImageDto findPartyRoomMainImageByPartyRoom(PartyRoom partyRoom) {
        PartyRoomImageDto partyRoomImageDto = partyRoomImageConverter.convertFromEntity(partyRoomImageRepository.findByPartyRoomAndImageType(partyRoom, PartyRoomImageType.MAIN).orElse(null));
        return setImageFileUrl(partyRoomImageDto);
    }

    public List<PartyRoomImageDto> findAllImagesByPartyRoom(PartyRoom partyRoom) {
        return partyRoomImageRepository.findAllByPartyRoom(partyRoom).stream()
                .map(partyRoomImageConverter::convertFromEntity)
                .map(this::setImageFileUrl)
                .toList();
    }

    public void modifyPartyRoomImageFile(PartyRoomImageDto before, PartyRoomImageDto after) {
        PartyRoomImage beforeEntity = partyRoomImageConverter.convertFromDto(before);
        if (StringUtils.hasText(after.getImageFileName())) {
            beforeEntity.modifyImageFileName(after.getImageFileName());
        }
    }

    @Transactional
    public void registerPartyRoomImage(MultipartFile imageFile, PartyRoomImageType imageType, PartyRoomDto partyRoomDto) {
        S3ObjectDto s3ObjectDto = s3ImageUploader.uploadFileWithRandomFilename(imageFile);
        PartyRoomImageDto mainImageDto = createImageDto(s3ObjectDto.getFileKey(), imageType);
        mainImageDto.setPartyRoomDto(partyRoomDto);
        partyRoomImageRepository.save(partyRoomImageConverter.convertFromDto(mainImageDto));
    }

    @Transactional
    public void deleteAllByPartyRoom(PartyRoom partyRoom) {
        int deleteCount = partyRoomImageRepository.deleteAllByPartyRoom(partyRoom);
        log.info("[PartyRoomImageService - deleteAll] successfully deleted {} images for partyRoomId: {}, ", deleteCount, partyRoom.getPartyRoomId());
    }

    private PartyRoomImageDto setImageFileUrl(PartyRoomImageDto partyRoomImageDto) {
        String imageFileUrl = s3ImageUploader.getResourceUrl(partyRoomImageDto.getImageFileName());
        partyRoomImageDto.setImageFileName(imageFileUrl);
        return partyRoomImageDto;
    }

    private PartyRoomImageDto createImageDto(String imageFileName, PartyRoomImageType imageType) {
        return PartyRoomImageDto.builder()
                .imageFileName(imageFileName)
                .partyRoomImageType(imageType)
                .build();
    }
}
