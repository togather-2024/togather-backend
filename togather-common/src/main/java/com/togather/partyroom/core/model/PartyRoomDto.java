package com.togather.partyroom.core.model;

import com.togather.member.model.MemberDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomDto {
    private String partyRoomName;
    private MemberDto partyRoomHost;
    private String partyRoomDesc;
    private long partyRoomViewCount;
    private int openingHour;
    private int closingHour;
    private long price;
    private int guestCapacity;
}
