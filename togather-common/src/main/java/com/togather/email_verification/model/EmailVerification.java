package com.togather.email_verification.model;

import com.togather.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long emailVerificationId;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration_time", columnDefinition = "DATETIME")
    private LocalDateTime verificationExpirationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverEmailAddress", referencedColumnName = "email")
    private Member member;

}
