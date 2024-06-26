package com.togather.email_verification.service;

import com.togather.common.exception.ErrorCode;
import com.togather.common.exception.TogatherApiException;
import com.togather.email_verification.converter.EmailVerificationConverter;
import com.togather.email_verification.dto.EmailVerificationDto;
import com.togather.email_verification.model.EmailVerification;
import com.togather.member.model.MemberDto;
import com.togather.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import com.togather.email_verification.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final EmailVerificationConverter emailVerificationDtoConverter;

    @Transactional
    public void registerEmailVerification(EmailVerificationDto emailVerificationDto) {
        String verificationCode = createVerificationCode();
        emailVerificationDto.from(verificationCode);

        EmailVerification emailVerification = emailVerificationDtoConverter.convertToEntity(emailVerificationDto);
        emailVerificationRepository.save(emailVerification);

        log.info("save into email_verification: {}", emailVerification.getReceiverEmailAddress());

        sendEmailVerification(emailVerificationDto);
    }

    public void sendEmailVerification(EmailVerificationDto emailVerificationDto) {
        MimeMessage message = createEmailForm(emailVerificationDto);
        javaMailSender.send(message);

        log.info("email sending to {} ", emailVerificationDto.getReceiverEmailAddress());
    }

    public MimeMessage createEmailForm(EmailVerificationDto emailVerificationDto) {
        if (isEmailDuplicated(emailVerificationDto.getReceiverEmailAddress()))
            throw new TogatherApiException(ErrorCode.EMAIL_SEND_DUPLICATE_MEMBER); //TODO: 예외 클래스 수정

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setFrom(new InternetAddress("to-gather@to-gather.com", "to:gather", "UTF-8"));
            message.setRecipients(MimeMessage.RecipientType.TO, emailVerificationDto.getReceiverEmailAddress());
            message.setSubject("to:gather 인증번호입니다.");
            String body = "<h3>" + "====to:gather====" + "</h3>" +
                    "<h3>" + "요청하신 인증번호입니다." + "</h3>" +
                    "<h1>" + emailVerificationDto.getVerificationCode() + "</h1>" +
                    "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    public String createVerificationCode() {
        return String.valueOf((int) (Math.random() * (90000)) + 100000);
    }

    public boolean isEmailDuplicated(String email) {
        return memberRepository.findByEmail(email).isEmpty() ? false : true; //false - 사용 가능, true - 사용 불가능(중복)
    }

    public void verifyEmailVerificationCode(EmailVerificationDto emailVerificationDto) {
        EmailVerification findEmailVerification = emailVerificationRepository.findLatestByEmail(emailVerificationDto.getReceiverEmailAddress())
                .orElseThrow(() -> new TogatherApiException(ErrorCode.EMAIL_VERIFY_NO_TRIAL));

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(findEmailVerification.getVerificationExpirationTime())) {
            throw new TogatherApiException(ErrorCode.EMAIL_VERIFY_EXPIRED_CODE);
        }
         if (!findEmailVerification.getVerificationCode().equals(emailVerificationDto.getVerificationCode())) {
            throw new TogatherApiException(ErrorCode.EMAIL_VERIFY_WRONG_CODE);
        }

        log.info("email verification successfully completed.: {}", emailVerificationDto.getReceiverEmailAddress());
    }
}
