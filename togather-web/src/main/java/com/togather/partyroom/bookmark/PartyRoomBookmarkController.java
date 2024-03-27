package com.togather.partyroom.bookmark;

import com.togather.partyroom.bookmark.model.PartyRoomBookmarkDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/partyroom")
@Tag(name = "Party Room Bookmark")
public class PartyRoomBookmarkController {
    private final PartyRoomBookmarkService partyRoomBookmarkService;

    @Operation(summary = "Set/remove Party Room Bookmark", description = "파티룸 등록 API")
    @ApiResponse(responseCode = "200", description = "success")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but has no HOST role", content = @Content)
    public ResponseEntity<String> upsert(@RequestBody PartyRoomBookmarkDto partyRoomBookmarkDto) {
        partyRoomBookmarkService.upsert(partyRoomBookmarkDto);
        return ResponseEntity.ok("ok");
    }
}
