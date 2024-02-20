package com.togather.member.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import com.togather.member.model.MemberInfoDto;
import com.togather.member.model.Role;
import com.togather.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberConverter memberConverter;
    private final MemberRepository memberRepository;

    @Transactional
    public void register(MemberDto memberDto) {
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
                .memberName(findMember.getMemberName())
                .role(findMember.getRole())
                .prifolePicFile(findMember.getProfilePicFile())
//                .partyRoomReservationDtos() TODO: partyRoomReservationDtos 추가
                .build();
    }

    @Transactional
    public void update(MemberDto memberDto) {
        Member findMember = memberRepository.findByEmail(memberDto.getEmail())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 추후 수정

        findMember.update(memberDto.getMemberName(), memberDto.getPassword(), memberDto.getProfilePicFile());

        log.info("update member info: {}", findMember.getMemberSrl());
    }

}
