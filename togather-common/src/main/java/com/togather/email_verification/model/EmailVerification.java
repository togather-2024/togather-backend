    package com.togather.email_verification.model;

    import jakarta.persistence.*;
    import lombok.Builder;
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

        @Column(name ="receiver_email_address")
        private String receiverEmailAddress;

        @Builder
        public EmailVerification(String verificationCode, LocalDateTime verificationExpirationTime, String receiverEmailAddress) {
            this.verificationCode = verificationCode;
            this.verificationExpirationTime = verificationExpirationTime;
            this.receiverEmailAddress = receiverEmailAddress;
        }

    }
