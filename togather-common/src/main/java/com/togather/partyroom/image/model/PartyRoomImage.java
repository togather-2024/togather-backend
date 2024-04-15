package com.togather.partyroom.image.model;

import com.togather.partyroom.core.model.PartyRoom;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "party_room_images")
public class PartyRoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long partyRoomImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @Column(name = "image_file_name")
    private String imageFileName;

    @Column(name = "type", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private PartyRoomImageType imageType;


    @Builder
    public PartyRoomImage(long partyRoomImageId, PartyRoom partyRoom, String imageFileName, PartyRoomImageType imageType) {
        this.partyRoomImageId = partyRoomImageId;
        this.partyRoom = partyRoom;
        this.imageFileName = imageFileName;
        this.imageType = imageType;
    }

    public void modifyImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}
