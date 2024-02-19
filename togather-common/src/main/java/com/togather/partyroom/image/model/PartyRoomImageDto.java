package com.togather.partyroom.image.model;

import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PartyRoomImageDto {
    private PartyRoomDto partyRoomDto;
    private String imageFileName;
    private PartyRoomImageType partyRoomImageType;
}
