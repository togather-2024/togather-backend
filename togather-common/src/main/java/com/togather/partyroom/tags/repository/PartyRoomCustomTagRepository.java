package com.togather.partyroom.tags.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartyRoomCustomTagRepository extends JpaRepository<PartyRoomCustomTag, Long> {
    PartyRoomCustomTag findByTagContent(String tagContent);
}
