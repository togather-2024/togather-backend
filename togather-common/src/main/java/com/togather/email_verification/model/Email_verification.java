package com.togather.email_verification.model;

import com.togather.member.model.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Email_verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long email_verification_id;
    private String receiver_email_address;
    private String verification_code;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime verification_expiration_time;
    @ManyToOne
    @JoinColumn(name = "receiver_email_address")
    private Member member;

}
