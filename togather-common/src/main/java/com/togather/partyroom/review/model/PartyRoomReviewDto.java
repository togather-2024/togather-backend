package com.togather.partyroom.review.model;

import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyRoomReviewDto {
    private long reviewId;
    private PartyRoomDto partyRoomDto;
    private MemberDto reviewer;
    private PartyRoomReservationDto partyRoomReservationDto;
    private String reviewDesc;
    private LocalDateTime createdAt;

    @Builder
    @Getter
    public static class Request {
        private long partyRoomReservationId;
        private String reviewDesc;
    }
}
