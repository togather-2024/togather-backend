package com.togather.partyroom.bookmark.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
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
    public void bookmark(MemberDto memberDto, long partyRoomId) {
        Member member = memberConverter.convertToEntity(memberDto);
        PartyRoom partyRoom = partyRoomService.findById(partyRoomId);

        PartyRoomBookmark partyRoomBookmark = PartyRoomBookmark.builder()
                .member(member)
                .partyRoom(partyRoom)
                .build();

        if (partyRoomBookmarkRepository.findByMemberAndPartyRoom(member, partyRoom) != null)
            throw new TogatherApiException(ErrorCode.DUPLICATED_BOOKMARK);

        partyRoomBookmarkRepository.save(partyRoomBookmark);

        log.info("bookmarked party room - member: {}, party_room_id: {}", member.getMemberSrl(), partyRoomId);
    }

    @Transactional
    public void unbookmark(MemberDto memberDto, long partyRoomId) {
        Member member = memberConverter.convertToEntity(memberDto);
        PartyRoom partyRoom = partyRoomService.findById(partyRoomId);

        PartyRoomBookmark partyRoomBookmark = partyRoomBookmarkRepository.findByMemberAndPartyRoom(member, partyRoom)
                .orElseThrow(() -> new TogatherApiException(ErrorCode.NOT_FOUND_BOOKMARK));

        partyRoomBookmarkRepository.delete(partyRoomBookmark);

        log.info("unbookmarked party room - member: {}, party_room_id: {}", member.getMemberSrl(), partyRoomId);
    }
}
