package com.togather.partyroom.location.repository;

import com.togather.partyroom.location.model.PartyRoomLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRoomLocationRepository extends JpaRepository<PartyRoomLocation, Long> {
}
