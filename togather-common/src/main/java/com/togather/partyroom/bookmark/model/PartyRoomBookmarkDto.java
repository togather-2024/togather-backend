package com.togather.partyroom.bookmark.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.image.model.PartyRoomImageDto;
import com.togather.partyroom.location.model.PartyRoomLocationDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartyRoomBookmarkDto {

    private long bookmarkId;

    private MemberDto memberDto;

    private PartyRoomDto partyRoomDto;

    private PartyRoomImageDto partyRoomImageDto;

    private final PartyRoomLocationDto partyRoomLocationDto;

}
