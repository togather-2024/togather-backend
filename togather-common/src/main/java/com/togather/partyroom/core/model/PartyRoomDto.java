package com.togather.partyroom.core.model;

import com.togather.member.model.MemberDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
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
