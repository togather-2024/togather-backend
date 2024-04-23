package com.togather.partyroom.review;

import com.togather.common.response.AddJsonFilters;
import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import com.togather.partyroom.review.service.PartyRoomReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.togather.common.response.ResponseFilter.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Tag(name = "Review CRUD")
public class PartyRoomReviewController {
    private final PartyRoomReviewService partyRoomReviewService;
    private final MemberService memberService;

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @Operation(summary = "Review List by member", description = "자신이 남긴 리뷰를 조회할 수 있는 API")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReviewDto.class)))
    @AddJsonFilters(filters = {MEMBER_DTO_EXCLUDE_PASSWORD, REVIEW_HIDE_DUPLICATE_INFO_IN_RESERVATION})
    public MappingJacksonValue getMyReviews(@RequestParam(required = false, defaultValue = "1") int pageNum,
                                            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return new MappingJacksonValue(partyRoomReviewService.findAllByReviewer(loginUser, PageRequest.of(pageNum - 1, pageSize)));
    }

    @GetMapping("/partyroom/{partyRoomId}")
    @Operation(summary = "Review List by party room", description = "특정 파티룸에 대한 리뷰를 조회할 수 있는 API - 누구나 조회 가능")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReviewDto.class)))
    @AddJsonFilters(filters = {MEMBER_DTO_EXCLUDE_PASSWORD, REVIEW_HIDE_DUPLICATE_INFO_IN_RESERVATION})
    public MappingJacksonValue getReviewsOfPartyRoom(@PathVariable("partyRoomId") long partyRoomId,
                                                     @RequestParam(required = false, defaultValue = "1") int pageNum,
                                                     @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return new MappingJacksonValue(partyRoomReviewService.findAllByPartyRoom(partyRoomId, PageRequest.of(pageNum - 1, pageSize)));
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Register review", description = "리뷰 작성 기능 - 예약 당 한번만 작성 가능하며, 예약 시간이 종료된 이후에 작성 가능")
    @ApiResponse(content = @Content(schema = @Schema(implementation = PartyRoomReviewDto.class)))
    @AddJsonFilters(filters = {MEMBER_DTO_EXCLUDE_PII, REVIEW_HIDE_DUPLICATE_INFO_IN_RESERVATION})
    public MappingJacksonValue register(PartyRoomReviewDto.Request review) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return new MappingJacksonValue(partyRoomReviewService.registerReview(review, loginUser));
    }

    @PostMapping("/check/qualification")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Check if user can leave review", description = "리뷰 작성 가능 여부 확인")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    public boolean checkQualification(long partyRoomReservationId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return partyRoomReviewService.canReview(partyRoomReservationId, loginUser);
    }

    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "delete review", description = "리뷰 삭제 API")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but trying to delete other user's review", content = @Content)
    public ResponseEntity<String> deleteReview(@PathVariable long reviewId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        PartyRoomReviewDto partyRoomReviewDto = partyRoomReviewService.findDtoById(reviewId);

        if (loginUser.getMemberSrl() != partyRoomReviewDto.getReviewer().getMemberSrl()) {
            throw new AccessDeniedException("cannot delete different user's review");
        }

        partyRoomReviewService.deleteReview(reviewId);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/modify/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @ApiResponse(responseCode = "401", description = "user not logged in (No JWT token)", content = @Content)
    @ApiResponse(responseCode = "403", description = "user is logged in but trying to modify other user's review", content = @Content)
    @Operation(summary = "modify review", description = "리뷰 수정 API")
    public void modifyReview(@PathVariable long reviewId, String reviewDesc) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        PartyRoomReviewDto partyRoomReviewDto = partyRoomReviewService.findDtoById(reviewId);
        if (loginUser.getMemberSrl() != partyRoomReviewDto.getReviewer().getMemberSrl()) {
            throw new AccessDeniedException("cannot modify different user's review");
        }

        partyRoomReviewService.modifyReview(reviewId, reviewDesc);
    }
}
