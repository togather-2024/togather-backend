package com.togather.member.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberDto {

    private long memberSrl;

    private String memberName;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private Role role;

    private String email;

    private String profilePicFile;

}
