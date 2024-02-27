package com.togather.partyroom.core.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomOperationDay;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyRoomOperationDayRepository extends JpaRepository<PartyRoomOperationDay, Long> {

    @Query("select p from PartyRoomOperationDay p where p.partyRoom = :partyRoom")
    List<PartyRoomOperationDay> findByPartyRoom(PartyRoom partyRoom);
}
