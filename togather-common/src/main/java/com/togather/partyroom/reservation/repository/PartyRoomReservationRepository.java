package com.togather.partyroom.reservation.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PartyRoomReservationRepository extends JpaRepository<PartyRoomReservation, Long> {

    @Query("select p from PartyRoomReservation p where p.reservationGuest.memberSrl = :reservationGuestSrl")
    List<PartyRoomReservation> findAllByGuest(long reservationGuestSrl);

    @Query("select p from PartyRoomReservation p where p.endTime >= :startTime and p.startTime <= :endTime")
    List<PartyRoomReservation> findByDateTimeReserved(LocalDateTime startTime, LocalDateTime endTime);

    @Query("select p from PartyRoomReservation p where p.partyRoom = :partyRoom and function('DATE', p.startTime) = :localDate")
    List<PartyRoomReservation> findByPartyRoomAndDate(PartyRoom partyRoom, LocalDate localDate);
}
