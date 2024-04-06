package com.togather.partyroom.bookmark.converter;

import com.togather.member.converter.MemberConverter;
import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import com.togather.partyroom.bookmark.model.PartyRoomBookmarkDto;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomBookmarkConverter {

    private final MemberConverter memberConverter;
    private final PartyRoomConverter partyRoomConverter;

    public PartyRoomBookmarkDto convertToDto(PartyRoomBookmark partyRoomBookmark) {
        if (partyRoomBookmark == null)
            return null;

        return PartyRoomBookmarkDto.builder()
                .bookmarkId(partyRoomBookmark.getBookmarkId())
                .memberDto(memberConverter.convertToDto(partyRoomBookmark.getMember()))
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomBookmark.getPartyRoom()))
                .build();
    }

    public PartyRoomBookmark convertToEntity(PartyRoomBookmarkDto partyRoomBookmarkDto) {
        if (partyRoomBookmarkDto == null)
            return null;

        return PartyRoomBookmark.builder()
                .bookmarkId(partyRoomBookmarkDto.getBookmarkId())
                .member(memberConverter.convertToEntity(partyRoomBookmarkDto.getMemberDto()))
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomBookmarkDto.getPartyRoomDto()))
                .build();
    }
}
