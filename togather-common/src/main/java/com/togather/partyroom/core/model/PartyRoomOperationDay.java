package com.togather.partyroom.core.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Entity
@Table(name = "party_room_operation_days")
@Getter
@NoArgsConstructor
public class PartyRoomOperationDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long operationDaysId;

    @ManyToOne
    @JoinColumn(name = "party_room_id")
    private PartyRoom partyRoom;

    @Column(name = "operation_day", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private DayOfWeek operationDay;

    @Builder
    public PartyRoomOperationDay(long operationDaysId, PartyRoom partyRoom, DayOfWeek operationDay) {
        this.operationDaysId = operationDaysId;
        this.partyRoom = partyRoom;
        this.operationDay = operationDay;
    }
}
