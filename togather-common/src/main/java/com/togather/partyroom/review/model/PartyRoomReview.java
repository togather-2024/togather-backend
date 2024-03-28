package com.togather.partyroom.review.model;

import com.togather.member.model.Member;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "party_room_review")
@Getter
@NoArgsConstructor
public class PartyRoomReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_srl")
    private Member reviewer;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private PartyRoomReservation partyRoomReservation;

    @Column(name ="review_desc")
    private String reviewDesc;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    @Builder
    PartyRoomReview(long reviewId, PartyRoom partyRoom, Member reviewer, PartyRoomReservation partyRoomReservation, String reviewDesc, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.partyRoom = partyRoom;
        this.reviewer = reviewer;
        this.partyRoomReservation = partyRoomReservation;
        this.reviewDesc = reviewDesc;
        this.createdAt = createdAt;
    }

    public void modifyReviewDesc(String reviewDesc) {
        this.reviewDesc = reviewDesc;
    }
}
