package com.togather.member.converter;

import com.togather.common.s3.S3ImageUploader;
import com.togather.member.model.Member;
import com.togather.member.model.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberConverter {

    private final S3ImageUploader s3ImageUploader;

    public MemberDto convertToDto(Member member) {
        if (member == null)
            return null;

        return MemberDto.builder()
                .memberSrl(member.getMemberSrl())
                .memberName(member.getMemberName())
                .password(member.getPassword())
                .role(member.getRole())
                .email(member.getEmail())
                .profilePicFile(s3ImageUploader.getResourceUrl(member.getProfilePicFile()))
                .build();
    }

    public Member convertToEntity(MemberDto memberDto) {
        if (memberDto == null)
            return null;
        return Member.builder()
                .memberSrl(memberDto.getMemberSrl())
                .memberName(memberDto.getMemberName())
                .password(memberDto.getPassword())
                .role(memberDto.getRole())
                .email(memberDto.getEmail())
                .profilePicFile(memberDto.getProfilePicFile())
                .build();
    }
}
