package com.togather.member.model;

import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoDto {
    private String prifolePicFile;
    private String memberName;
    private PartyRoomReservationDto partyRoomReservationDto;
    private Role role;

}
