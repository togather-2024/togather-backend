package com.togather.partyroom.review.model;

import com.togather.member.model.Member;
import com.togather.partyroom.core.model.PartyRoom;
import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @ManyToOne
    @JoinColumn(name = "reviewer_srl")
    private Member reviewer;

    @Column(name ="review_desc")
    private String reviewDesc;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}
