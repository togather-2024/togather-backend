package com.togather.partyroom.core.model;

import com.togather.member.model.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party_room")
@Getter
@NoArgsConstructor
public class PartyRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long partyRoomId;

    @Column(name = "party_room_name")
    private String partyRoomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_host_srl")
    private Member partyRoomHost;

    @Column(name = "party_room_desc")
    private String partyRoomDesc;

    @Column(name = "party_room_view_count")
    private long partyRoomViewCount;

    @Column(name = "opening_hour")
    private int openingHour;

    @Column(name = "closingHour")
    private int closingHour;

    @Column(name = "price")
    private long price;

    @Column(name = "guestCapacity")
    private int guestCapacity;
    @Builder
    public PartyRoom(long partyRoomId, String partyRoomName, Member partyRoomHost, String partyRoomDesc, long partyRoomViewCount, int openingHour, int closingHour, long price, int guestCapacity) {
        this.partyRoomId = partyRoomId;
        this.partyRoomName = partyRoomName;
        this.partyRoomHost = partyRoomHost;
        this.partyRoomDesc = partyRoomDesc;
        this.partyRoomViewCount = partyRoomViewCount;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.price = price;
        this.guestCapacity = guestCapacity;
    }

    public void modifyPartyRoom(PartyRoom after) {
        this.partyRoomName = after.getPartyRoomName();
        this.partyRoomDesc = after.getPartyRoomDesc();
        this.openingHour = after.getOpeningHour();
        this.closingHour = after.getClosingHour();
        this.price = after.getPrice();
        this.guestCapacity = after.getGuestCapacity();
    }
}
