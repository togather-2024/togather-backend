package com.togather.partyroom.review.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.partyroom.core.converter.PartyRoomConverter;
import com.togather.partyroom.core.model.PartyRoomDto;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservation;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import com.togather.partyroom.review.converter.PartyRoomReviewConverter;
import com.togather.partyroom.review.model.PartyRoomReview;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import com.togather.partyroom.review.repository.PartyRoomReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartyRoomReviewService {
    private final PartyRoomReviewRepository partyRoomReviewRepository;
    private final PartyRoomReviewConverter partyRoomReviewConverter;
    private final MemberConverter memberConverter;
    private final PartyRoomReservationConverter partyRoomReservationConverter;
    private final PartyRoomReservationService partyRoomReservationService;

    @Transactional(readOnly = true)
    public PartyRoomReviewDto findByPartyRoomAndReviewer(PartyRoomReservationDto partyRoomReservationDto, MemberDto reviewer) {
        PartyRoomReservation partyRoomReservation = partyRoomReservationConverter.convertToEntity(partyRoomReservationDto);
        Member member = memberConverter.convertToEntity(reviewer);

        return partyRoomReviewConverter.convertFromEntity(partyRoomReviewRepository.findByPartyRoomReservationAndReviewer(partyRoomReservation, member));
    }

    @Transactional(readOnly = true)
    public List<PartyRoomReviewDto> findAllByReviewer(MemberDto reviewer, PageRequest pageRequest) {
        return partyRoomReviewRepository.findAllByReviewer(memberConverter.convertToEntity(reviewer), pageRequest)
                .stream().map(partyRoomReviewConverter::convertFromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<PartyRoomReviewDto> findAllByPartyRoom(long partyRoomId, PageRequest pageRequest) {
        return partyRoomReviewRepository.findAllByPartyRoomId(partyRoomId, pageRequest)
                .stream().map(partyRoomReviewConverter::convertFromEntity).toList();
    }

    @Transactional(readOnly = true)
    public long countByPartyRoom(long partyRoomId) {
        return partyRoomReviewRepository.countByPartyRoomId(partyRoomId);
    }

    @Transactional(readOnly = true)
    public PartyRoomReviewDto findDtoById(long reviewId) {
        return partyRoomReviewConverter.convertFromEntity(findById(reviewId));
    }

    @Transactional(readOnly = true)
    public PartyRoomReview findById(long reviewId) {
        return partyRoomReviewRepository.findById(reviewId)
                .orElseThrow(() -> new TogatherApiException(ErrorCode.REVIEW_NOT_FOUND));
    }


    @Transactional
    public PartyRoomReviewDto registerReview(PartyRoomReviewDto.Request partyRoomReviewDtoRequest, MemberDto reviewer) {
        PartyRoomReservationDto partyRoomReservationDto = partyRoomReservationService.findDtoByReservationId(partyRoomReviewDtoRequest.getPartyRoomReservationId()).getPartyRoomReservationDto();
        PartyRoomDto partyRoomDto = partyRoomReservationDto.getPartyRoomDto();

        if (!isCorrectReviewer(partyRoomReservationDto, reviewer)) {
            throw new TogatherApiException(ErrorCode.REVIEW_DIFFERENT_MEMBER);
        }

        if (!hasFinishedPartyRoomReservation(partyRoomReservationDto)) {
            throw new TogatherApiException(ErrorCode.REVIEW_RESERVATION_NOT_COMPLETE);
        }

        if (hasAlreadyReviewed(partyRoomReservationDto, reviewer)) {
            throw new TogatherApiException(ErrorCode.DUPLICATE_REVIEW_TRIAL);
        }

        PartyRoomReviewDto partyRoomReviewDto = PartyRoomReviewDto.builder()
                .reviewDesc(partyRoomReviewDtoRequest.getReviewDesc())
                .partyRoomDto(partyRoomDto)
                .partyRoomReservationDto(partyRoomReservationDto)
                .reviewer(reviewer)
                .createdAt(LocalDateTime.now())
                .build();

        PartyRoomReview saved = partyRoomReviewRepository.save(partyRoomReviewConverter.convertFromDto(partyRoomReviewDto));

        return partyRoomReviewConverter.convertFromEntity(saved);
    }

    @Transactional(readOnly = true)
    public boolean canReview(long partyRoomReservationId, MemberDto memberDto) {
        PartyRoomReservationDto partyRoomReservationDto = partyRoomReservationService.findDtoByReservationId(partyRoomReservationId).getPartyRoomReservationDto();
        return isCorrectReviewer(partyRoomReservationDto, memberDto)
                && hasFinishedPartyRoomReservation(partyRoomReservationDto)
                && !hasAlreadyReviewed(partyRoomReservationDto, memberDto);
    }

    private boolean isCorrectReviewer(PartyRoomReservationDto partyRoomReservationDto, MemberDto reviewer) {
        return partyRoomReservationDto.getReservationGuestDto().getMemberSrl() == reviewer.getMemberSrl();
    }

    private boolean hasFinishedPartyRoomReservation(PartyRoomReservationDto partyRoomReservationDto) {
        return LocalDateTime.now().isAfter(partyRoomReservationDto.getEndTime());
    }

    private boolean hasAlreadyReviewed(PartyRoomReservationDto partyRoomReservationDto, MemberDto reviewer) {
        return findByPartyRoomAndReviewer(partyRoomReservationDto, reviewer) != null;
    }


    @Transactional
    public void modifyReview(long reviewId, String reviewDesc) {
        PartyRoomReview partyRoomReview = findById(reviewId);
        partyRoomReview.modifyReviewDesc(reviewDesc);
        log.info("[PartyRoomReviewService] modified review for reviewId: {}", reviewId);
    }

    @Transactional
    public void deleteReview(long reviewId) {
        partyRoomReviewRepository.deleteById(reviewId);
        log.info("[PartyRoomReviewService] deleted review for reviewId: {}", reviewId);
    }
}
