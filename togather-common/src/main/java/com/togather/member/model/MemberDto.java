package com.togather.member.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.validation.constraints.Email;
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

    @Email
    private String email;

    private String profilePicFile;

    @Getter
    public static class UpdatePassword {
        private String previousPassword;
        private String newPassword;
        private String confirmNewPassword;
    }

    @Getter
    public static class Withdraw {
        private String password;
    }
}
