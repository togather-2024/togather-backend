package com.togather.member.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonFilter("MemberDtoFilter")
public class MemberDto {

    private long memberSrl;

    private String memberName;

    private String password;

    private Role role;

    private String email;

    private String profilePicFile;

}
