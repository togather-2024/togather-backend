package com.togather.partyroom.review.repository;

import com.togather.member.model.Member;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.review.model.PartyRoomReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyRoomReviewRepository extends JpaRepository<PartyRoomReview, Long> {

    PartyRoomReview findByPartyRoomReservationAndReviewer(PartyRoomReservation partyRoomReservation, Member reviewer);

    List<PartyRoomReview> findAllByReviewer(Member reviewer);

    @Query("select r from PartyRoomReview r where r.partyRoom.partyRoomId = :partyRoomId")
    List<PartyRoomReview> findAllByPartyRoomId(long partyRoomId);
}
