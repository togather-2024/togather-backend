package com.togather.partyroom.tags.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party_room_custom_tags")
@Getter
@NoArgsConstructor
public class PartyRoomCustomTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tagId;

    @Column(name = "tag_content")
    private String tagContent;

    @Column(name = "tag_count")
    private long tagCount;

    public void incrementTagCount() {
        this.tagCount += 1;
    }

    public void decrementTagCount() {
        this.tagCount -= 1;
    }

    @Builder
    public PartyRoomCustomTag(long tagId, String tagContent, long tagCount) {
        this.tagId = tagId;
        this.tagContent = tagContent;
        this.tagCount = tagCount;
    }
}
