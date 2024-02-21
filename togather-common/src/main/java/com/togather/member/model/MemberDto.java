package com.togather.member.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDto {
    private long memberSrl;
    private String memberName;
    private String password;
    private Role role;
    private String email;
    private String profilePicFile;

}
