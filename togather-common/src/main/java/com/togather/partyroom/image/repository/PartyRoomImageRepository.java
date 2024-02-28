package com.togather.partyroom.image.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.image.model.PartyRoomImage;
import com.togather.partyroom.image.model.PartyRoomImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartyRoomImageRepository extends JpaRepository<PartyRoomImage, Long> {
    Optional<PartyRoomImage> findByPartyRoomAndImageType(PartyRoom partyRoom, PartyRoomImageType imageType);

    List<PartyRoomImage> findAllByPartyRoom(PartyRoom partyRoom);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PartyRoomImage p where p.partyRoom = :partyRoom")
    int deleteAllByPartyRoom(PartyRoom partyRoom);
}
