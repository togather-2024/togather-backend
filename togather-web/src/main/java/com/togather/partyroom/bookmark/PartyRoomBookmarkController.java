package com.togather.partyroom.bookmark;

import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.bookmark.service.PartyRoomBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partyroom")
@Tag(name = "Party Room Bookmark")
public class PartyRoomBookmarkController {

    private final PartyRoomBookmarkService partyRoomBookmarkService;
    private final MemberService memberService;

    @Operation(summary = "Bookmark Party Room", description = "파티룸 즐겨찾기 등록 API")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @PostMapping("/{partyroom-id}")
    public ResponseEntity<String> bookmark(@PathVariable("partyroom-id") long partyRoomId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        partyRoomBookmarkService.bookmark(loginUser, partyRoomId);
        return ResponseEntity.ok("ok");
    }

    @Operation(summary = "Unbookmark Party Room", description = "파티룸 즐겨찾기 삭제 API")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    @DeleteMapping("/{partyroom-id}")
    public ResponseEntity<String> unbookmark(@PathVariable("partyroom-id") long partyRoomId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        partyRoomBookmarkService.unbookmark(loginUser, partyRoomId);
        return ResponseEntity.ok("ok");
    }
}
