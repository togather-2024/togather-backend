package com.togather.partyroom.core.repository;

import com.togather.partyroom.core.model.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long>, PartyRoomCustomRepository {
}
