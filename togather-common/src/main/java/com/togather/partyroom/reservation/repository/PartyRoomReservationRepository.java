package com.togather.partyroom.reservation.repository;

import com.togather.partyroom.reservation.model.PartyRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRoomReservationRepository extends JpaRepository<PartyRoomReservation, Long> {
}
