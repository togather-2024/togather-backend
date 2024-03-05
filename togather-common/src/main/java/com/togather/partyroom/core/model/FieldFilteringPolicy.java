package com.togather.partyroom.core.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum FieldFilteringPolicy {

    MEMBER_DTO_HIDE_PII_DATA_TEST(MemberDto.class, "memberName, password", List.of()),

    PARTY_ROOM_DTO_HIDE_PII_DATA_TEST(PartyRoomDto.class, "", List.of(Pair.of("partyRoomHost", MEMBER_DTO_HIDE_PII_DATA_TEST))),

    PARTY_ROOM_RESERVATION_DTO_PII_DATA_TEST(PartyRoomReservationDto.class, "paymentStatus",
            List.of(Pair.of("partyRoomDto", PARTY_ROOM_DTO_HIDE_PII_DATA_TEST), Pair.of("reservationGuestDto", MEMBER_DTO_HIDE_PII_DATA_TEST)));


    private final Class clazz;
    private final String fieldsToExclude;
    private final List<Pair<String, FieldFilteringPolicy>> childFieldPolicies;
}
