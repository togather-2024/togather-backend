package com.togather.partyroom.location.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.location.model.PartyRoomLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRoomLocationRepository extends JpaRepository<PartyRoomLocation, Long> {
    Optional<PartyRoomLocation> findByPartyRoom(PartyRoom partyRoom);
}
