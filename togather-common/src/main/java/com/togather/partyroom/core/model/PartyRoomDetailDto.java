package com.togather.partyroom.core.model;

import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import com.togather.partyroom.tags.model.PartyRoomCustomTagDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
@Getter
public class PartyRoomDetailDto {
    private final PartyRoomDto partyRoomDto;
    private final PartyRoomLocationDto partyRoomLocationDto;
    private final List<PartyRoomImageDto> partyRoomImageDtoList;
    private final List<PartyRoomOperationDayDto> operationDays;
    private final List<PartyRoomCustomTagDto> customTags;
    private final boolean isBookmarked;
}
