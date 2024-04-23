package com.togather.partyroom.bookmark.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import com.togather.partyroom.bookmark.model.PartyRoomBookmarkDto;
import com.togather.partyroom.bookmark.repository.PartyRoomBookmarkRepository;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyRoomBookmarkService {

    private final PartyRoomBookmarkRepository partyRoomBookmarkRepository;
    private final MemberConverter memberConverter;
    private final PartyRoomConverter partyRoomConverter;

    @Transactional
    public void bookmark(MemberDto memberDto, PartyRoom partyRoom) {
        Member member = memberConverter.convertToEntity(memberDto);

        PartyRoomBookmark partyRoomBookmark = PartyRoomBookmark.builder()
                .member(member)
                .partyRoom(partyRoom)
                .build();

        PartyRoomBookmark existBookmark = partyRoomBookmarkRepository.findByMemberAndPartyRoom(member, partyRoom).orElse(null);
        if (existBookmark != null)
            throw new TogatherApiException(ErrorCode.DUPLICATED_BOOKMARK);

        partyRoomBookmarkRepository.save(partyRoomBookmark);

        log.info("bookmarked party room - member: {}, party_room_id: {}", member.getMemberSrl(), partyRoom.getPartyRoomId());
    }

    @Transactional
    public void unbookmark(MemberDto memberDto, PartyRoom partyRoom) {
        Member member = memberConverter.convertToEntity(memberDto);

        PartyRoomBookmark partyRoomBookmark = partyRoomBookmarkRepository.findByMemberAndPartyRoom(member, partyRoom)
                .orElseThrow(() -> new TogatherApiException(ErrorCode.NOT_FOUND_BOOKMARK));

        partyRoomBookmarkRepository.delete(partyRoomBookmark);

        log.info("unbookmarked party room - member: {}, party_room_id: {}", member.getMemberSrl(), partyRoom.getPartyRoomId());
    }

    public List<PartyRoomBookmarkDto> findAllByMember(MemberDto memberDto) {
        Member member = memberConverter.convertToEntity(memberDto);

        List<PartyRoomBookmarkDto> partyRoomBookmarkDtoList = partyRoomBookmarkRepository.findAllByMember(member)
                .stream().map(p -> PartyRoomBookmarkDto.builder()
                        .bookmarkId(p.getBookmarkId())
                        .memberDto(memberDto)
                        .partyRoomDto(partyRoomConverter.convertFromEntity(p.getPartyRoom()))
                        .build())
                .toList();

        log.info("find all bookmark list by member: {}", memberDto.getMemberSrl());

        return partyRoomBookmarkDtoList;
    }

    public boolean hasBookmarked(MemberDto memberDto, PartyRoom partyRoom) {
        Member member = memberConverter.convertToEntity(memberDto);

        return partyRoomBookmarkRepository.findByMemberAndPartyRoom(member, partyRoom) != null;
    }

    public long countByPartyRoom(long partyRoomId) {
        return partyRoomBookmarkRepository.countByPartyRoomId(partyRoomId);
    }
}
