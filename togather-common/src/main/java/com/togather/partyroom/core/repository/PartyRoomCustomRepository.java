package com.togather.partyroom.core.repository;

import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.model.PartyRoomSearchQueryDto;

import java.util.List;

public interface PartyRoomCustomRepository {
    List<PartyRoom> search(PartyRoomSearchQueryDto partyRoomSearchQueryDto);
}
