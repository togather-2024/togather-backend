package com.togather.common.response;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseFilter {

    MEMBER_DTO_EXCLUDE_PII(MemberDto.class, new String[] {"memberName", "password", "profilePicFile"}),
    PARTY_ROOM_DTO_SIMPLE(PartyRoomDto.class, new String[] {"partyRoomDesc"}),
//    PARTY_ROOM_RESERVATION_DTO(PartyRoomReservationDto.class, new String[]{}),
    PARTY_ROOM_RESERVATION_RESPONSE_DTO_SIMPLE(PartyRoomReservationResponseDto.class, new String[]{"reservationGuestDto", "guestCount"}),
    PARTY_ROOM_IMAGE_DTO_SIMPLE(PartyRoomImageDto.class, new String[]{"partyRoomImageId", "partyRoomImageType"}),
    MEMBER_DTO_FOR_RESERVATION(MemberDto.class, new String[]{"memberSrl", "password", "role", "email"}),
    REVIEW_HIDE_DUPLICATE_INFO_IN_RESERVATION(PartyRoomReservationDto.class, new String[]{"partyRoomDto", "reservationGuestDto"})
    ;

    private final Class<?> targetClass;
    private final String[] fieldsToExclude;
}
