package com.togather.partyroom.reservation.repository;

import com.togather.member.model.Member;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyRoomReservationRepository extends JpaRepository<PartyRoomReservation, Long> {

    @Query("select p from PartyRoomReservation p where p.reservationGuest = :member")
    List<PartyRoomReservation> findAllByGuest(Member member);
}
