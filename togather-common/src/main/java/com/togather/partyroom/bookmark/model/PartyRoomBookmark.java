package com.togather.partyroom.bookmark.model;

import com.togather.member.model.Member;
import com.togather.partyroom.core.model.PartyRoom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "party_room_bookmark")
public class PartyRoomBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberSrl", referencedColumnName = "memberSrl")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partyRoomId", referencedColumnName = "partyRoomId")
    private PartyRoom partyRoom;

    @Builder
    public PartyRoomBookmark(Member member, PartyRoom partyRoom) {
        this.member = member;
        this.partyRoom = partyRoom;
    }
}
