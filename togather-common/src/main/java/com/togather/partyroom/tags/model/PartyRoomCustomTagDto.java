package com.togather.partyroom.tags.model;

import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PartyRoomCustomTagDto {
    private PartyRoomDto partyRoomDto;
    private String tagContent;
    private long tagCount;
}
