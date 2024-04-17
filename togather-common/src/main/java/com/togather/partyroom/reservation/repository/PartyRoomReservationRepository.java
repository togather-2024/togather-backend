package com.togather.partyroom.reservation.repository;

import com.togather.member.model.Member;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface PartyRoomReservationRepository extends JpaRepository<PartyRoomReservation, Long> {

    @Query("select p from PartyRoomReservation p where p.reservationGuest.memberSrl = :reservationGuestSrl")
    List<PartyRoomReservation> findAllByGuest(long reservationGuestSrl);

    @Query("select p from PartyRoomReservation p where p.endTime >= :startTime and p.startTime <= :endTime and (p.paymentStatus = 'PENDING' or p.paymentStatus = 'COMPLETE')")
    List<PartyRoomReservation> findByDateTimeReserved(Timestamp startTime, Timestamp endTime);

    @Query("select p from PartyRoomReservation p where p.partyRoom = :partyRoom and function('DATE', p.startTime) = :localDate")
    List<PartyRoomReservation> findByPartyRoomAndDate(PartyRoom partyRoom, LocalDate localDate);

    PartyRoomReservation findByPartyRoomAndReservationGuestOrderByEndTimeDesc(PartyRoom partyRoom, Member reservationGuest);
}
