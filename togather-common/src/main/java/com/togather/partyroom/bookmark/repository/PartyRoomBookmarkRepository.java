package com.togather.partyroom.bookmark.repository;

import com.togather.member.model.Member;
import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import com.togather.partyroom.core.model.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartyRoomBookmarkRepository extends JpaRepository<PartyRoomBookmark, Long> {

    @Query("select p from PartyRoomBookmark p where p.member = :member and p.partyRoom = :partyRoom")
    Optional<PartyRoomBookmark> findByMemberAndPartyRoom(Member member, PartyRoom partyRoom);

    @Query("select p from PartyRoomBookmark p where p.member = :member")
    List<PartyRoomBookmark> findAllByMember(Member member);
}