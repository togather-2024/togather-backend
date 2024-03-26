package com.togather.review;

import com.togather.member.model.MemberDto;
import com.togather.member.service.MemberService;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import com.togather.partyroom.review.service.PartyRoomReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class PartyRoomReviewController {
    private final PartyRoomReviewService partyRoomReviewService;
    private final MemberService memberService;

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public List<PartyRoomReviewDto> getMyReviews() {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return partyRoomReviewService.findAllByReviewer(loginUser);
    }

    @GetMapping("/partyroom/{partyRoomId}")
    public List<PartyRoomReviewDto> getReviewsOfPartyRoom(@PathVariable("partyRoomId") long partyRoomId) {
        return partyRoomReviewService.findAllByPartyRoom(partyRoomId);
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public PartyRoomReviewDto register(PartyRoomReviewDto.Request review) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return partyRoomReviewService.registerReview(review, loginUser);
    }

    @PostMapping("/check/qualification")
    @PreAuthorize("isAuthenticated()")
    public boolean checkQualification(long partyRoomReservationId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        return partyRoomReviewService.canReview(partyRoomReservationId, loginUser);
    }

    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public void deleteReview(@PathVariable long reviewId) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        PartyRoomReviewDto partyRoomReviewDto = partyRoomReviewService.findDtoById(reviewId);

        if (loginUser.getMemberSrl() != partyRoomReviewDto.getReviewer().getMemberSrl()) {
            throw new AccessDeniedException("cannot delete different user's review");
        }

        partyRoomReviewService.deleteReview(reviewId);
    }

    @PostMapping("/modify/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public void modifyReview(@PathVariable long reviewId, String reviewDesc) {
        MemberDto loginUser = memberService.findByAuthentication(SecurityContextHolder.getContext().getAuthentication());
        PartyRoomReviewDto partyRoomReviewDto = partyRoomReviewService.findDtoById(reviewId);
        if (loginUser.getMemberSrl() != partyRoomReviewDto.getReviewer().getMemberSrl()) {
            throw new AccessDeniedException("cannot modify different user's review");
        }

        partyRoomReviewService.modifyReview(reviewId, reviewDesc);
    }
}
