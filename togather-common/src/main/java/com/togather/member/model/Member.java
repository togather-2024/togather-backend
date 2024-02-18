package com.togather.member.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_srl;
    private String member_name;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private String email;
    private String profile_pic_file;
    private boolean email_verified;

}
