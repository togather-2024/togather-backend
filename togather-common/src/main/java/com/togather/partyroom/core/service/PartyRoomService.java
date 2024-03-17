package com.togather.partyroom.core.service;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDetailDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.core.model.PartyRoomOperationDayDto;
import com.togather.partyroom.core.repository.PartyRoomRepository;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.image.service.PartyRoomImageService;
import com.togather.partyroom.location.model.PartyRoomLocation;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.location.service.PartyRoomLocationService;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import com.togather.partyroom.tags.service.PartyRoomCustomTagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyRoomService {
    private final PartyRoomConverter partyRoomConverter;
    private final PartyRoomRepository partyRoomRepository;
    private final PartyRoomCustomTagService partyRoomCustomTagService;
    private final PartyRoomOperationDayService partyRoomOperationDayService;
    private final PartyRoomLocationService partyRoomLocationService;
    private final PartyRoomImageService partyRoomImageService;


    public PartyRoomDto findPartyRoomDtoById(long partyRoomId) {
        return partyRoomConverter.convertFromEntity(findById(partyRoomId));
    }

    public void modifyPartyRoomCore(PartyRoomDto before, PartyRoomDto after) {
        PartyRoom beforeEntity = partyRoomConverter.convertFromDto(before);
        beforeEntity.modifyPartyRoom(partyRoomConverter.convertFromDto(after));
    }

    @Transactional
    public PartyRoomDto register(PartyRoomDto partyRoomDto, List<PartyRoomCustomTagDto> customTags, PartyRoomLocationDto partyRoomLocationDto,
                                 PartyRoomImageDto mainPartyRoomImageDto, List<PartyRoomOperationDayDto> operationDayDtoList) {

        // Save party room basic data and persist to get PK of entity
        PartyRoom savedPartyRoomEntity = partyRoomRepository.save(partyRoomConverter.convertFromDto(partyRoomDto));
        partyRoomDto.setPartyRoomId(savedPartyRoomEntity.getPartyRoomId());

        // Save location data
        partyRoomLocationDto.setPartyRoomDto(partyRoomDto);
        partyRoomLocationService.registerLocation(partyRoomLocationDto);

        // Save operation day data
        operationDayDtoList.stream().forEach(operationDayDto -> operationDayDto.setPartyRoomDto(partyRoomDto));
        partyRoomOperationDayService.registerOperationDays(operationDayDtoList);

        // Save main party room image only when file exists
        // TODO: Add image file from controller
        if (StringUtils.hasText(mainPartyRoomImageDto.getImageFileName())) {
            mainPartyRoomImageDto.setPartyRoomDto(partyRoomDto);
            partyRoomImageService.registerPartyRoomMainImage(null, mainPartyRoomImageDto);
        }

        // Save tag related data
        partyRoomCustomTagService.registerTags(partyRoomDto, customTags);

        return partyRoomDto;
    }

    @Transactional
    public PartyRoomDto modifyPartyRoom(PartyRoomDto afterPartyRoomDto, List<PartyRoomCustomTagDto> afterCustomTags, PartyRoomLocationDto afterPartyRoomLocationDto,
                                        PartyRoomImageDto afterMainPartyRoomImageDto, List<PartyRoomOperationDayDto> afterOperationDayDtoList) {

        // Modify partyRoom Core info(e.g. name/desc/price/open/close/guestCapacity)
        PartyRoomDto beforePartyRoomDto = findPartyRoomDtoById(afterPartyRoomDto.getPartyRoomId());
        modifyPartyRoomCore(beforePartyRoomDto, afterPartyRoomDto);

        // Get original location info and replace the info to the parameters input by client
        PartyRoomLocationDto beforeLocationDto = partyRoomLocationService.findLocationDtoByPartyRoom(partyRoomConverter.convertFromDto(afterPartyRoomDto));
        partyRoomLocationService.modifyPartyRoomLocation(beforeLocationDto, afterPartyRoomLocationDto);

        // Get original main image and replace the imageFile to inputImage by client
        PartyRoomImageDto beforeImageDto = partyRoomImageService.findPartyRoomMainImageByPartyRoom(partyRoomConverter.convertFromDto(afterPartyRoomDto));
        partyRoomImageService.modifyPartyRoomImageFile(beforeImageDto, afterMainPartyRoomImageDto);

        // Get original operation days and modify to input by client
        List<PartyRoomOperationDayDto> beforeOperationDayDtoList = partyRoomOperationDayService.findOperationDaysByPartyRoom(partyRoomConverter.convertFromDto(afterPartyRoomDto));
        partyRoomOperationDayService.modifyOperationDays(beforeOperationDayDtoList, afterOperationDayDtoList);

        //Modify Tags related data
        partyRoomCustomTagService.modifyTags(afterPartyRoomDto, afterCustomTags);

        return null;
    }

    @Transactional
    public void deletePartyRoomById(long partyRoomId) {
        PartyRoom partyRoom = findById(partyRoomId);
        PartyRoomDto partyRoomDto = partyRoomConverter.convertFromEntity(partyRoom);

        // Remove location info
        PartyRoomLocation partyRoomLocation = partyRoomLocationService.findLocationByPartyRoom(partyRoom);
        partyRoomLocationService.deleteLocation(partyRoomLocation);

        // Remove image Files
        partyRoomImageService.deleteAllByPartyRoom(partyRoom);

        // Remove operationDays
        partyRoomOperationDayService.deleteAllOperationDaysByPartyRoom(partyRoom);

        // Remove custom tags
        // Not using bulk update because delete logic for M:N relation is complicated
        List<PartyRoomCustomTagDto> customTagDtoList = partyRoomCustomTagService.findCustomTagsByPartyRoom(partyRoom);
        partyRoomCustomTagService.removeTagsFromPartyRoom(partyRoomDto, customTagDtoList);

        // Remove PartyRoom core
        partyRoomRepository.delete(partyRoom);
        log.info("[PartyRoomService - delete] deleted partyRoom data and mapped entities. partyRoomId: {}", partyRoomId);
    }

    @Transactional
    public PartyRoomDetailDto findDetailDtoById(long partyRoomId) {
        PartyRoom partyRoom = findById(partyRoomId);
        PartyRoomDto partyRoomDto = partyRoomConverter.convertFromEntity(partyRoom);

        PartyRoomLocationDto partyRoomLocationDto = partyRoomLocationService.findLocationDtoByPartyRoom(partyRoom);
        List<PartyRoomImageDto> partyRoomImageDtoList = partyRoomImageService.findAllImagesByPartyRoom(partyRoom);
        List<PartyRoomOperationDayDto> operationDayDtoList = partyRoomOperationDayService.findOperationDaysByPartyRoom(partyRoom);
        List<PartyRoomCustomTagDto> customTagDtoList = partyRoomCustomTagService.findCustomTagsByPartyRoom(partyRoom);

        return new PartyRoomDetailDto(
                partyRoomDto,
                partyRoomLocationDto,
                partyRoomImageDtoList,
                operationDayDtoList,
                customTagDtoList
        );
    }

    public PartyRoom findById(long partyRoomId) {
        return partyRoomRepository.findById(partyRoomId)
                .orElseThrow(RuntimeException::new);
    }

}
