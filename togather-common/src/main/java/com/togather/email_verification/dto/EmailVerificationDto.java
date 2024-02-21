package com.togather.email_verification.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class EmailVerificationDto {
    private long emailVerificationId;
    private String receiverEmailAddress;
    private String verificationCode;
    private LocalDateTime verificationExpirationTime;

    public void from(String verificationCode) {
        LocalDateTime currentTime = LocalDateTime.now();
        long expirationMillis = 1800000; // 30분
        LocalDateTime expirationTime = currentTime.plus(expirationMillis, ChronoUnit.MILLIS); //현재시간 + 30분

        this.verificationExpirationTime = expirationTime;
        this.verificationCode = verificationCode;
    }
}
