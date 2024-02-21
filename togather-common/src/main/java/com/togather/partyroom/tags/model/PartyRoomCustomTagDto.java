package com.togather.partyroom.tags.model;

import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class PartyRoomCustomTagDto {
    @Setter
    private long tagId;
    private String tagContent;
    private long tagCount;
}
