package com.togather.member.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.common.s3.S3ImageUploader;
import com.togather.common.s3.S3ObjectDto;
import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.member.repository.MemberRepository;
import com.togather.partyroom.bookmark.converter.PartyRoomBookmarkConverter;
import com.togather.partyroom.bookmark.model.PartyRoomBookmarkDto;
import com.togather.partyroom.bookmark.service.PartyRoomBookmarkService;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentDto;
import com.togather.partyroom.payment.service.PaymentService;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationResponseDto;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import com.togather.partyroom.review.service.PartyRoomReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberConverter memberConverter;
    private final MemberRepository memberRepository;
    private final S3ImageUploader s3ImageUploader;
    private final PartyRoomBookmarkService partyRoomBookmarkService;
    private final PartyRoomBookmarkConverter partyRoomBookmarkConverter;
    private final PartyRoomReservationService partyRoomReservationService;
    private final PartyRoomReservationConverter partyRoomReservationConverter;
    private final PartyRoomReviewService partyRoomReviewService;
    private final PaymentService paymentService;

    @Transactional

    public void register(MemberDto memberDto) {
        checkDuplicateMember(memberDto.getEmail());

        Member member = memberConverter.convertToEntity(memberDto);
        memberRepository.save(member);

        log.info("save into member: {}", member.getEmail());
    }

    public void login(MemberDto memberDto) {
        Member findMember = memberRepository.findByEmailAndPassword(memberDto.getEmail(), memberDto.getPassword())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 추후 수정

        log.info("member logged in: {}", findMember.getEmail());
    }

    @Transactional
    public void delete(MemberDto.Withdraw withdraw) {
        MemberDto findMember = findByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        //패스워드 불일치 예외 처리
        if (!findMember.getPassword().equals(withdraw.getPassword()))
            throw new TogatherApiException(ErrorCode.PASSWORD_MISMATCH);

        //처리중인 예약이 있으면 예외 처리
        List<PartyRoomReservationResponseDto> reservationList = partyRoomReservationService.findAllByMember(findMember);
        reservationList.stream()
                .filter(reservation -> partyRoomReservationService.hasFinishedPartyRoomReservation(reservation.getPartyRoomReservationDto()))
                .findAny()
                .ifPresent(reservation -> {
                    throw new TogatherApiException(ErrorCode.EXIST_RESERVATION);
                });

        //자식 테이블 데이터 일괄 삭제(북마크, 예약, 결제, 리뷰)
        List<PartyRoomReviewDto> reviewList = partyRoomReviewService.findAllByReviewer(findMember);
        reviewList.stream()
                .map(PartyRoomReviewDto::getReviewId)
                .forEach(partyRoomReviewService::deleteReview);

        List<Payment> paymentList = paymentService.findAllByMember(memberConverter.convertToEntity(findMember));
        paymentList.stream()
                .forEach(paymentService::deletePayment);

        reservationList.stream()
                .map(PartyRoomReservationResponseDto::getPartyRoomReservationDto)
                .map(reservation -> partyRoomReservationConverter.convertToEntity(reservation))
                .forEach(partyRoomReservationService::delete);

        List<PartyRoomBookmarkDto> bookmarkList = partyRoomBookmarkService.findAllByMember(findMember);
        bookmarkList.stream()
                .map(bookmark -> partyRoomBookmarkConverter.convertToEntity(bookmark))
                .forEach(bookmark -> partyRoomBookmarkService.unbookmark(findMember, bookmark.getPartyRoom()));

        //회원 삭제
        memberRepository.delete(memberConverter.convertToEntity(findMember));

        log.info("delete member: {}", findMember.getMemberSrl());
    }

    public MemberDto findMemberDtoById(long id) {
        return memberConverter.convertToDto(memberRepository.findById(id)
                .orElseThrow(RuntimeException::new));
    }

    public Member findById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    /*
    This method is used when login is required.
     */
    public MemberDto findByAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return memberConverter.convertToDto(memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("cannot find user by authentication")
        ));
    }


    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(RuntimeException::new);
    }

    public Member findMemberByAuthentication(Authentication authentication) {
        String userEmail = authentication.getName();
        return memberRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("cannot find user by authentication")
        );
    }

    /*
    This method is used when login is optional - just to check
     */
    public MemberDto findNullableByAuthentication(Authentication authentication) {
        try {
            return findByAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }

    public void checkDuplicateMember(String email) {
        Member alreadyJoinMember = memberRepository.findByEmail(email).orElse(null);
        if (alreadyJoinMember != null) {
            throw new TogatherApiException(ErrorCode.JOIN_DUPLICATE_MEMBER);
        }
    }

    @Transactional
    public void updateName(String name) {
        Member findMember = findMemberByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        findMember.updateName(name);
        log.info("update member name: {}", findMember.getMemberSrl());
    }

    @Transactional
    public void updatePassword(MemberDto.UpdatePassword updatePassword) {
        Member findMember = findMemberByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        if (!findMember.getPassword().equals(updatePassword.getPreviousPassword()))
            throw new TogatherApiException(ErrorCode.PREVIOUS_PASSWORD_MISMATCH);
        if (!updatePassword.getNewPassword().equals(updatePassword.getConfirmNewPassword()))
            throw new TogatherApiException(ErrorCode.NEW_PASSWORD_MISMATCH);

        findMember.updatePassword(updatePassword.getNewPassword());

        log.info("update member password: {}", findMember.getMemberSrl());
    }

    @Transactional
    public void updateProfileImage(MultipartFile profileImage) {
        Member findMember = findMemberByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        S3ObjectDto s3ObjectDto = s3ImageUploader.uploadFileWithRandomFilename(profileImage);
        findMember.updateProfilePicFile(s3ObjectDto.getFileKey());

        log.info("update member profile image: {}", findMember.getMemberSrl());
    }
}
