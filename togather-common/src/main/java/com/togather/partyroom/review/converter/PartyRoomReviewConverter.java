package com.togather.partyroom.review.converter;

import com.togather.member.converter.MemberConverter;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.review.model.PartyRoomReview;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartyRoomReviewConverter {
    private final PartyRoomConverter partyRoomConverter;
    private final MemberConverter memberConverter;
    private final PartyRoomReservationConverter partyRoomReservationConverter;

    public PartyRoomReviewDto convertFromEntity(PartyRoomReview partyRoomReview) {
        if (partyRoomReview == null) {
            return null;
        }

        return PartyRoomReviewDto.builder()
                .reviewId(partyRoomReview.getReviewId())
                .partyRoomDto(partyRoomConverter.convertFromEntity(partyRoomReview.getPartyRoom()))
                .reviewer(memberConverter.convertToDto(partyRoomReview.getReviewer()))
                .partyRoomReservationDto(partyRoomReservationConverter.convertToDto(partyRoomReview.getPartyRoomReservation()))
                .reviewDesc(partyRoomReview.getReviewDesc())
                .createdAt(partyRoomReview.getCreatedAt())
                .build();
    }

    public PartyRoomReview convertFromDto(PartyRoomReviewDto partyRoomReviewDto) {
        if (partyRoomReviewDto == null) {
            return null;
        }

        return PartyRoomReview.builder()
                .reviewId(partyRoomReviewDto.getReviewId())
                .partyRoom(partyRoomConverter.convertFromDto(partyRoomReviewDto.getPartyRoomDto()))
                .reviewer(memberConverter.convertToEntity(partyRoomReviewDto.getReviewer()))
                .partyRoomReservation(partyRoomReservationConverter.convertToEntity(partyRoomReviewDto.getPartyRoomReservationDto()))
                .reviewDesc(partyRoomReviewDto.getReviewDesc())
                .createdAt(partyRoomReviewDto.getCreatedAt())
                .build();
    }
}
