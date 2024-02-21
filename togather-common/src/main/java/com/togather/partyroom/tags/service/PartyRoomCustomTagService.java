package com.togather.partyroom.tags.service;

import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.tags.converter.PartyRoomCustomTagConverter;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRel;
import com.togather.partyroom.tags.repository.PartyRoomCustomTagRelRepository;
import com.togather.partyroom.tags.repository.PartyRoomCustomTagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyRoomCustomTagService {
    private final PartyRoomCustomTagRepository partyRoomCustomTagRepository;
    private final PartyRoomCustomTagRelRepository partyRoomCustomTagRelRepository;
    private final PartyRoomCustomTagConverter partyRoomCustomTagConverter;

    @Transactional
    public void registerTag(PartyRoomDto partyRoomDto, PartyRoomCustomTagDto partyRoomCustomTagDto) {
        // If tag with content does not exist - create one and get PK by inserting data
        // If tag with content already exists - increment count of original tag
        PartyRoomCustomTag customTag = partyRoomCustomTagRepository.findByTagContent(partyRoomCustomTagDto.getTagContent());
        if (customTag == null) {
            customTag = partyRoomCustomTagRepository.save(partyRoomCustomTagConverter.convertFromDto(partyRoomCustomTagDto));
        } else {
            customTag.incrementTagCount();
        }
        partyRoomCustomTagDto.setTagId(customTag.getTagId());

        // Always save relation between partyRoom & customTag
        PartyRoomCustomTagRel relation = partyRoomCustomTagConverter.convertFromDto(partyRoomDto, partyRoomCustomTagDto);
        partyRoomCustomTagRelRepository.save(relation);
    }

    @Transactional
    public void registerTags(PartyRoomDto partyRoomDto, List<PartyRoomCustomTagDto> customTagDtoList) {
        customTagDtoList.stream()
                .forEach(customTagDto -> registerTag(partyRoomDto, customTagDto));
    }
}
