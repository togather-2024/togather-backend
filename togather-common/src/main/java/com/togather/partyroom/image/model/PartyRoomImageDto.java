package com.togather.partyroom.image.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
@JsonFilter("PartyRoomImageDtoFilter")
public class PartyRoomImageDto {
    private long partyRoomImageId;
    @Setter @JsonIgnore
    private PartyRoomDto partyRoomDto;
    @Setter
    private String imageFileName;
    private PartyRoomImageType partyRoomImageType;

    public void setToMainImage() {
        this.partyRoomImageType = PartyRoomImageType.MAIN;
    }
}
