package com.togather.member.model;

import com.togather.partyroom.bookmark.model.PartyRoomBookmark;
import com.togather.partyroom.core.model.PartyRoom;
import com.togather.partyroom.payment.model.Payment;
import jakarta.persistence.*;
import lombok.Builder;
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

    @OneToMany(mappedBy = "partyRoomHost", fetch = FetchType.LAZY)
    private List<PartyRoom> partyRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Payment> paymentList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<PartyRoomBookmark> bookmarkList = new ArrayList<>();

    @Builder
    public Member(long memberSrl, String memberName, String password, Role role, String email, String profilePicFile) {
        this.memberSrl = memberSrl;
        this.memberName = memberName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.profilePicFile = profilePicFile;
    }

    public void updateName(String memberName) {
        this.memberName = memberName;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

}
