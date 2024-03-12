package com.togather.common;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ResponseFilter {

    MEMBER_DTO_EXCLUDE_PII(MemberDto.class, new String[] {"memberName", "password", "profilePicFile"}),
    PARTY_ROOM_DTO_SIMPLE(PartyRoomDto.class, new String[] {"partyRoomDesc"}),
    PARTY_ROOM_RESERVATION_DTO(PartyRoomReservationDto.class, new String[]{});

    private final Class<?> targetClass;
    private final String[] fieldsToExclude;
}
