package com.togather.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
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

    public MemberDto findMemberDtoById(Long memberSrl) {
        MemberDto findMemberDto = memberConverter.convertToDto(memberRepository.findById(memberSrl)
                .orElseThrow(RuntimeException::new)); //TODO: 예외 클래스 추후 수정

        log.info("search member info: {}", memberSrl);

        return findMemberDto;
    }

    public String serializeDto(MemberDto memberDto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            FilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter("MemberDtoFilter", SimpleBeanPropertyFilter.filterOutAllExcept("memberName", "email", "profilePicFile"));

            objectMapper.setFilterProvider(filterProvider);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(memberDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("parsing error", e);
        }
    }



    @Transactional
    public void update(MemberDto memberDto) {
        Member findMember = memberRepository.findByEmail(memberDto.getEmail())
                .orElseThrow(RuntimeException::new); //TODO: 예외 클래스 추후 수정

        findMember.update(memberDto.getMemberName(), memberDto.getPassword(), memberDto.getProfilePicFile());

        log.info("update member info: {}", findMember.getMemberSrl());
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

}
