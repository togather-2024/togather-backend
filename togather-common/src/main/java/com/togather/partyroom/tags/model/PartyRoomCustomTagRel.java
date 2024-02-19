package com.togather.partyroom.tags.model;

import com.togather.partyroom.core.model.PartyRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party_room_tags_rel")
@Getter
@NoArgsConstructor
public class PartyRoomCustomTagRel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long partyRoomTagId;

    @ManyToOne
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private PartyRoomCustomTag partyRoomCustomTag;
}
