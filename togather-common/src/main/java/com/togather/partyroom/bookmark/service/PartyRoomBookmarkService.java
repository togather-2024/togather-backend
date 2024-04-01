package com.togather.partyroom.bookmark.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import com.togather.partyroom.bookmark.repository.PartyRoomBookmarkRepository;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.core.service.PartyRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRoomBookmarkService {

    private final PartyRoomBookmarkRepository partyRoomBookmarkRepository;
    private final PartyRoomService partyRoomService;
    private final MemberConverter memberConverter;

    @Transactional
    public void register(MemberDto memberDto, long partyRoomId) {
        Member member = memberConverter.convertToEntity(memberDto);
        PartyRoom partyRoom = partyRoomService.findById(partyRoomId);

        PartyRoomBookmark partyRoomBookmark = PartyRoomBookmark.builder()
                .member(member)
                .partyRoom(partyRoom)
                .build();

        partyRoomBookmarkRepository.save(partyRoomBookmark);

        log.info("register party room bookmark, member:{}, party_room_id: {}", member.getMemberSrl(), partyRoom.getPartyRoomId());
    }
}
