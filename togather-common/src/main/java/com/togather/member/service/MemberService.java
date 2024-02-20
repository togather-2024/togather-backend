package com.togather.member.service;

import com.togather.member.converter.MemberConverter;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
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
    public void join(MemberDto memberDto) {
        Member member = memberConverter.convertToEntity(memberDto);
        memberRepository.save(member);

        log.info("save into member: {}", member.getEmail());

    }

}
