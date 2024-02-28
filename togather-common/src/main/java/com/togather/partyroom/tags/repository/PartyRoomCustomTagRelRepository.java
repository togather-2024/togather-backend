package com.togather.partyroom.tags.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.tags.model.PartyRoomCustomTag;
import com.togather.partyroom.tags.model.PartyRoomCustomTagRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PartyRoomCustomTagRelRepository extends JpaRepository<PartyRoomCustomTagRel, Long> {
    @Query("select r.partyRoomCustomTag from PartyRoomCustomTagRel r where r.partyRoom = :partyRoom")
    List<PartyRoomCustomTag> findCustomTagsByPartyRoom(PartyRoom partyRoom);

    void deleteByPartyRoomAndPartyRoomCustomTag(PartyRoom partyRoom, PartyRoomCustomTag customTag);
}
