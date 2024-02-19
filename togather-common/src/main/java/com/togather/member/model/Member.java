package com.togather.member.model;

import com.togather.email_verification.model.EmailVerification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberSrl;

    @Column(name = "member_name")
    private String memberName;

    @Column(name ="password")
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_pic_file")
    private String profilePicFile;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EmailVerification> emailVerificationList = new ArrayList<>();

}
