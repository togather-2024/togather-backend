package com.togather.partyroom.tags.service;

import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.tags.converter.PartyRoomCustomTagConverter;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRel;
import com.togather.partyroom.tags.repository.PartyRoomCustomTagRelRepository;
import com.togather.partyroom.tags.repository.PartyRoomCustomTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyRoomCustomTagService {
    private final PartyRoomCustomTagRepository partyRoomCustomTagRepository;
    private final PartyRoomCustomTagRelRepository partyRoomCustomTagRelRepository;
    private final PartyRoomCustomTagConverter partyRoomCustomTagConverter;
    private final PartyRoomConverter partyRoomConverter;


    public List<PartyRoomCustomTagDto> findCustomTagsByPartyRoom(PartyRoom partyRoom) {
        return partyRoomCustomTagRelRepository.findCustomTagsByPartyRoom(partyRoom).stream()
                .map(partyRoomCustomTagConverter::convertFromEntity).collect(Collectors.toList());
    }

    public List<PartyRoomCustomTagDto> findCustomTagsByPartyRoomDto(PartyRoomDto partyRoomDto) {
        return findCustomTagsByPartyRoom(partyRoomConverter.convertFromDto(partyRoomDto));
    }

    @Transactional
    public void registerTag(PartyRoomDto partyRoomDto, PartyRoomCustomTagDto partyRoomCustomTagDto) {
        // If tag with content does not exist - create one and get PK by inserting data
        // If tag with content already exists - increment count of original tag
        PartyRoomCustomTag customTag = partyRoomCustomTagRepository.findByTagContent(partyRoomCustomTagDto.getTagContent());
        if (customTag == null) {
            partyRoomCustomTagDto.setInitialTagCount();
            customTag = partyRoomCustomTagRepository.save(partyRoomCustomTagConverter.convertFromDto(partyRoomCustomTagDto));
            log.info("[PartyRoomCustomTagService - register] successfully registered new tag. content: {}", customTag.getTagContent());
        } else {
            log.info("[PartyRoomCustomTagService - register] incremented count for tag with content: {}", customTag.getTagContent());
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

        log.info("[PartyRoomCustomTagService - register] created {} relations between tags and partyRoomId: {}", customTagDtoList.size(), partyRoomDto.getPartyRoomId());
    }

    @Transactional
    public void removeTagFromPartyRoom(PartyRoomDto partyRoomDto, PartyRoomCustomTagDto partyRoomCustomTagDto) {
        // Always drop the relation between party room and custom tag
        PartyRoomCustomTag customTag = partyRoomCustomTagRepository.findByTagContent(partyRoomCustomTagDto.getTagContent());
        PartyRoom partyRoom = partyRoomConverter.convertFromDto(partyRoomDto);

        partyRoomCustomTagRelRepository.deleteByPartyRoomAndPartyRoomCustomTag(partyRoom, customTag);


        // If tagCount is still positive (tagCount > 0), leave the tag data
        // If tagCount becomes 0 -> It means the tag data should be deleted
        if (customTag.getTagCount() == 1) {
            partyRoomCustomTagRepository.delete(customTag);
            log.info("[PartyRoomCustomTagService - delete] deleted tag with content: {}", customTag.getTagContent());
        } else {
            log.info("[PartyRoomCustomTagService - delete] decremented tagCount with content: {}", customTag.getTagContent());
            customTag.decrementTagCount();
        }
    }

    @Transactional
    public void removeTagsFromPartyRoom(PartyRoomDto partyRoomDto, List<PartyRoomCustomTagDto> customTagDtoList) {
        customTagDtoList.stream().forEach(customTagDto -> removeTagFromPartyRoom(partyRoomDto, customTagDto));
        log.info("[PartyRoomCustomTagService - delete] removed {} relations between tags and partyRoomId: {}", customTagDtoList.size(), partyRoomDto.getPartyRoomId());
    }

    @Transactional
    public void modifyTags(PartyRoomDto partyRoomDto, List<PartyRoomCustomTagDto> afterList) {
        List<PartyRoomCustomTagDto> beforeList = findCustomTagsByPartyRoomDto(partyRoomDto);

        List<String> beforeTagContent = beforeList.stream().map(PartyRoomCustomTagDto::getTagContent).collect(Collectors.toList());
        List<String> afterTagContent = afterList.stream().map(PartyRoomCustomTagDto::getTagContent).collect(Collectors.toList());

        // Skip for contents both on before and after

        // Tags only on before: remove relation between tag and party room
        beforeList.stream().filter(beforeDto -> !afterTagContent.contains(beforeDto.getTagContent())).forEach(beforeDto -> removeTagFromPartyRoom(partyRoomDto, beforeDto));

        // Tags only on after: add relation between tag and party room
        afterList.stream().filter(afterDto -> !beforeTagContent.contains(afterDto.getTagContent())).forEach(afterDto -> registerTag(partyRoomDto, afterDto));
    }

    @Transactional(readOnly = true)
    public List<String> getPopularTags(int limit) {
        List<PartyRoomCustomTag> partyRoomCustomTags = partyRoomCustomTagRepository.findAllByOrderByTagCountDesc(Limit.of(limit));
        return partyRoomCustomTags.stream().map(PartyRoomCustomTag::getTagContent).toList();
    }
}
