package com.togather.member.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.common.s3.S3ImageUploader;
import com.togather.common.s3.S3ObjectDto;
import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.member.model.MemberInfoDto;
import com.togather.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberConverter memberConverter;
    private final MemberRepository memberRepository;
    private final S3ImageUploader s3ImageUploader;

    @Transactional
    public void register(MemberDto memberDto) {
        checkDuplicateMember(memberDto.getEmail());

        Member member = memberConverter.convertToEntity(memberDto); //TODO: Role(Enum) 세팅은 Controller에서
        memberRepository.save(member);

        log.info("save into member: {}", member.getEmail());
    }

    public void login(MemberDto memberDto) {
        Member findMember = memberRepository.findByEmailAndPassword(memberDto.getEmail(), memberDto.getPassword())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 추후 수정

        //TODO: jwt 토큰 발급 로직 구현

        log.info("member logged in: {}", findMember.getEmail());
    }

    public MemberInfoDto searchMemberInfo(Long memberSrl) {
        Member findMember = memberRepository.findById(memberSrl)
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 추후 수정

        log.info("search member info: {}", memberSrl);

        return MemberInfoDto.builder()
                .memberSrl(memberSrl)
                .memberName(findMember.getMemberName())
                .role(findMember.getRole())
                .profilePicFile(findMember.getProfilePicFile())
//                .partyRoomReservationDtos() TODO: partyRoomReservationDtos 추가
                .build();
    }

    @Transactional
    public void delete(Long memberSrl) {
        //TODO: 비밀번호 검증 로직 추가할 가능성 있음!!

        Member findMember = memberRepository.findById(memberSrl)
                .orElseThrow(RuntimeException::new);

        memberRepository.delete(findMember);

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
