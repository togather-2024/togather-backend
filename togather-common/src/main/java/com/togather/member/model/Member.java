package com.togather.member.model;

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

    @OneToMany(mappedBy = "partyRoomHost")
    private List<PartyRoom> partyRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private List<Payment> paymentList = new ArrayList<>();

    @Builder
    public Member(long memberSrl, String memberName, String password, Role role, String email, String profilePicFile) {
        this.memberSrl = memberSrl;
        this.memberName = memberName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.profilePicFile = profilePicFile;
    }

    public void update(String memberName, String password, String profilePicFile) {
        this.memberName = memberName;
        this.password = password;
        this.profilePicFile = profilePicFile;
    }

}
