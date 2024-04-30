package com.togather.member.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.common.s3.S3ImageUploader;
import com.togather.common.s3.S3ObjectDto;
import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.member.model.Role;
import com.togather.member.repository.MemberRepository;
import com.togather.partyroom.bookmark.converter.PartyRoomBookmarkConverter;
import com.togather.partyroom.bookmark.model.PartyRoomBookmarkDto;
import com.togather.partyroom.bookmark.service.PartyRoomBookmarkService;
import com.togather.partyroom.payment.model.Payment;
import com.togather.partyroom.payment.model.PaymentStatus;
import com.togather.partyroom.payment.service.PaymentService;
import com.togather.partyroom.reservation.converter.PartyRoomReservationConverter;
import com.togather.partyroom.reservation.model.PartyRoomReservationDto;
import com.togather.partyroom.reservation.model.PartyRoomReservationResponseDto;
import com.togather.partyroom.reservation.service.PartyRoomReservationService;
import com.togather.partyroom.review.model.PartyRoomReviewDto;
import com.togather.partyroom.review.service.PartyRoomReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class MemberService {
    @Autowired
    private MemberConverter memberConverter;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private S3ImageUploader s3ImageUploader;
    @Autowired
    private PartyRoomBookmarkService partyRoomBookmarkService;
    @Autowired
    private PartyRoomBookmarkConverter partyRoomBookmarkConverter;
    @Autowired @Lazy
    private PartyRoomReservationService partyRoomReservationService;
    @Autowired @Lazy
    private PartyRoomReservationConverter partyRoomReservationConverter;
    @Autowired @Lazy
    private PartyRoomReviewService partyRoomReviewService;
    @Autowired @Lazy
    private PaymentService paymentService;

    @Transactional
    public void register(MemberDto memberDto) {
        checkDuplicateMember(memberDto.getEmail());

        Member member = memberConverter.convertToEntity(memberDto);
        memberRepository.save(member);

        log.info("save into member: {}", member.getEmail());
    }

    @Transactional
    public void delete(MemberDto.Withdraw withdraw) {
        MemberDto findMember = findByAuthentication(SecurityContextHolder.getContext().getAuthentication());

        if (findMember.getRole() == Role.HOST)
            throw new TogatherApiException(ErrorCode.HOST_WITHDRAWAL_DISABLED);

        //패스워드 불일치 예외 처리
        if (!findMember.getPassword().equals(withdraw.getPassword()))
            throw new TogatherApiException(ErrorCode.PASSWORD_MISMATCH);

        //처리중인 예약이 있으면 예외 처리
        List<PartyRoomReservationResponseDto> reservationList = partyRoomReservationService.findAllByMember(findMember);
        reservationList.stream()
                .filter(reservation -> {
                    PartyRoomReservationDto reservationDto = reservation.getPartyRoomReservationDto();
                    PaymentStatus paymentStatus = reservationDto.getPaymentStatus();

                    //예약이 진행중일 경우 예외
                    if (paymentStatus == PaymentStatus.PENDING || //예약 등록 후 결제를 안 한 상태일 경우
                            (paymentStatus == PaymentStatus.COMPLETE &&
                            !partyRoomReservationService.hasFinishedPartyRoomReservation(reservationDto))) {
                        //결제를 마쳤지만 이용을 안 한 경우
                        throw new TogatherApiException(ErrorCode.EXIST_RESERVATION);
                    }

                    return false;
                })
                .findAny();

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
