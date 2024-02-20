package com.togather.partyroom.reservation.model;

import com.togather.member.model.Member;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.payment.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "party_room_reservation")
@NoArgsConstructor
@Getter
public class PartyRoomReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reservationId;

    @ManyToOne
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @ManyToOne
    @JoinColumn(name = "guest_srl")
    private Member reservationGuest;

    @Column(name = "guest_count")
    private int guestCount;

    @Column(name = "start_time", columnDefinition = "DATETIME")
    private LocalDateTime startTime;

    @Column(name = "end_time", columnDefinition = "DATETIME")
    private LocalDateTime endTime;

    @Column(name = "payment_status", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    public PartyRoomReservation(PartyRoom partyRoom, Member reservationGuest, int guestCount, LocalDateTime startTime, LocalDateTime endTime, PaymentStatus paymentStatus) {
        this.partyRoom = partyRoom;
        this.reservationGuest = reservationGuest;
        this.guestCount = guestCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentStatus = paymentStatus;
    }
}