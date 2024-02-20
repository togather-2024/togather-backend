package com.togather.member.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


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

    @Builder
    public Member(String memberName, String password, Role role, String email, String profilePicFile) {
        this.memberName = memberName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.profilePicFile = profilePicFile;
    }

}
