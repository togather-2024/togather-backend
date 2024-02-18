package com.togather.member.model;

import com.togather.email_verification.model.Email_verification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long member_srl;
    private String member_name;
    private String password;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private String email;
    private String profile_pic_file;
    @Column(insertable=false, updatable=false)
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Email_verification> emailVerificationList = new ArrayList<>();

}
