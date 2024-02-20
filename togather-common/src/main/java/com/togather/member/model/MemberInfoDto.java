package com.togather.member.model;

import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MemberInfoDto {
    private String prifolePicFile;
    private String memberName;
    private List<PartyRoomReservationDto> partyRoomReservationDtos;
    private Role role;

}
