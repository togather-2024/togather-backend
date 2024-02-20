package com.togather.partyroom.tags.repository;

import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRoomCustomTagRepository extends JpaRepository<PartyRoomCustomTag, Long> {
    PartyRoomCustomTag findByTagContent(String tagContent);
}
