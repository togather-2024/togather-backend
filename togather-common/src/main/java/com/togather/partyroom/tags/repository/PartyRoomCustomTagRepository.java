package com.togather.partyroom.tags.repository;

import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface PartyRoomCustomTagRepository extends JpaRepository<PartyRoomCustomTag, Long> {
    PartyRoomCustomTag findByTagContent(String tagContent);

    List<PartyRoomCustomTag> findAllByOrderByTagCountDesc(Limit limit);
}
