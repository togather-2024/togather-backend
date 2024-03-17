package com.togather.partyroom.core.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@JsonFilter("PartyRoomDtoFilter")
public class PartyRoomDto {
    @Setter
    private long partyRoomId;

    private String partyRoomName;

    @Setter
    private MemberDto partyRoomHost;

    private String partyRoomDesc;

    private long partyRoomViewCount;

    private int openingHour;

    private int closingHour;

    private long price;

    private int guestCapacity;
}
