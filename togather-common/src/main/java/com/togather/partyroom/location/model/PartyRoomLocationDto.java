package com.togather.partyroom.location.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PartyRoomLocationDto {
    private long partyRoomLocationId;
    @Setter @JsonIgnore
    private PartyRoomDto partyRoomDto;
    private String sido;
    private String sigungu;
    private String roadName;
    private String roadAddress;
    private String jibunAddress;
    private String detailAddress;
}
