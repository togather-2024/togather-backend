package com.togather.partyroom.location.model;

import com.togather.partyroom.core.model.PartyRoom;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "party_room_location")
public class PartyRoomLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long partyRoomLocationId;

    @OneToOne
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @Column(name = "sido")
    private String sido;

    @Column(name = "sigungu")
    private String sigungu;

    @Column(name = "road_name")
    private String roadName;

    @Column(name = "road_address")
    private String roadAddress;

    @Column(name = "jibun_address")
    private String jibunAddress;

    @Builder
    public PartyRoomLocation(long partyRoomLocationId, PartyRoom partyRoom, String sido, String sigungu, String roadName, String roadAddress, String jibunAddress) {
        this.partyRoomLocationId = partyRoomLocationId;
        this.partyRoom = partyRoom;
        this.sido = sido;
        this.sigungu = sigungu;
        this.roadName = roadName;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
    }

    public void modifyPartyRoomLocation(String sido, String sigungu, String roadName, String roadAddress, String jibunAddress) {
        this.sido = sido;
        this.sigungu = sigungu;
        this.roadName = roadName;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
    }

    public void modifyPartyRoomLocation(PartyRoomLocation after) {
        modifyPartyRoomLocation(after.getSido(), after.getSigungu(), after.getRoadName(), after.getRoadAddress(), after.getJibunAddress());
    }
}
