package com.togather.partyroom.bookmark.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PartyRoomBookmarkDto {

    private long bookmarkId;

    private long memberSrl;

    private long partyroomId;

    @Builder
    public PartyRoomBookmarkDto(long memberSrl, long partyroomId) {
        this.memberSrl = memberSrl;
        this.partyroomId = partyroomId;
    }
}
