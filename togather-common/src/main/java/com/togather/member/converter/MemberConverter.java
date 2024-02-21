package com.togather.member.converter;

import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {
    public MemberDto convertToDto(Member member) {
        return MemberDto.builder()
                .memberName(member.getMemberName())
                .password(member.getPassword())
                .role(member.getRole())
                .email(member.getEmail())
                .profilePicFile(member.getProfilePicFile())
                .build();
    }

    public Member convertToEntity(MemberDto memberDto) {
        return Member.builder()
                .memberName(memberDto.getMemberName())
                .password(memberDto.getPassword())
                .role(memberDto.getRole())
                .email(memberDto.getEmail())
                .profilePicFile(memberDto.getProfilePicFile())
                .build();
    }
}
