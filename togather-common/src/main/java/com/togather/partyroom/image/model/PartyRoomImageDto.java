package com.togather.partyroom.image.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
public class PartyRoomImageDto {
    private long partyRoomImageId;
    @Setter @JsonIgnore
    private PartyRoomDto partyRoomDto;
    private String imageFileName;
    private PartyRoomImageType partyRoomImageType;

    public void setToMainImage() {
        this.partyRoomImageType = PartyRoomImageType.MAIN;
    }
}
