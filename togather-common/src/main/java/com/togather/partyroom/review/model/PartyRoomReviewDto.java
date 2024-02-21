package com.togather.partyroom.review.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyRoomReviewDto {
    private long reviewId;
    private PartyRoomDto partyRoomDto;
    private MemberDto reviewer;
    private String reviewDesc;
    private LocalDateTime createdAt;
}
