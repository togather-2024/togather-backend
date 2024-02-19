package com.togather.email_verification.service;

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

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private static int number;
    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;

    public void sendEmail(String email) {
        MimeMessage message = createEmailForm(email); //TODO: 디비 삽입 로직 추가
        javaMailSender.send(message);
    }

    public MimeMessage createEmailForm(String email) {
        if (!isEmailDuplicated(email))
            throw new RuntimeException("중복된 이메일입니다: " + email); //TODO: 예외 클래스 수정

        createVerificationNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setFrom(new InternetAddress("to-gather@to-gather.com", "to:gather", "UTF-8"));
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("to:gather 인증번호입니다.");
            String body = "<h3>" + "====to:gather====" + "</h3>" +
                    "<h3>" + "요청하신 인증번호입니다." + "</h3>" +
                    "<h1>" + number + "</h1>" +
                    "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    public void createVerificationNumber() {
        number = (int) (Math.random() * (90000)) + 100000;
    }

    public boolean isEmailDuplicated(String email) {
        return memberRepository.findByEmail(email).isEmpty(); //true - 사용 가능, false - 사용 불가능(중복)
    }
}
