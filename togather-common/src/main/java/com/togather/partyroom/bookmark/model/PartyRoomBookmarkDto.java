package com.togather.partyroom.bookmark.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomBookmarkDto {

    private long bookmarkId;

    private MemberDto memberDto;

    private PartyRoomDto partyRoomDto;

}
