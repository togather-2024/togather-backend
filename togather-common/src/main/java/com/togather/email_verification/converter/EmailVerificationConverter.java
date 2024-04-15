package com.togather.email_verification.converter;

import com.togather.email_verification.dto.EmailVerificationDto;
import com.togather.email_verification.model.EmailVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailVerificationConverter {

    public EmailVerification convertToEntity(EmailVerificationDto emailVerificationDto) {
        if (emailVerificationDto == null)
            return null;

        return EmailVerification.builder()
                .emailVerificationId(emailVerificationDto.getEmailVerificationId())
                .receiverEmailAddress(emailVerificationDto.getReceiverEmailAddress())
                .verificationCode(emailVerificationDto.getVerificationCode())
                .verificationExpirationTime(emailVerificationDto.getVerificationExpirationTime())
                .build();
    }

}
