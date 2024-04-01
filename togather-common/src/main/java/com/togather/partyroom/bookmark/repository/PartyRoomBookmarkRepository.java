package com.togather.partyroom.bookmark.repository;

import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRoomBookmarkRepository extends JpaRepository<PartyRoomBookmark, Long> {
}
