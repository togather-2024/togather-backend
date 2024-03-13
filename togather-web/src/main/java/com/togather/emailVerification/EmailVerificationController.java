package com.togather.emailVerification;

import com.togather.email_verification.dto.EmailVerificationDto;
import com.togather.email_verification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "Email Verification")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping()
    @Operation(summary = "Email Verification Code Sending API", description = "이메일 인증 코드 전송 API")
    public ResponseEntity<String> sendEmailVerification(@Valid @RequestBody EmailVerificationDto emailVerificationDto) {
        emailVerificationService.registerEmailVerification(emailVerificationDto);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/validity")
    @Operation(summary = "Email verification code validation", description = "이메일 인증 코드 검증 API")
    public ResponseEntity<String> verifyEmailVerificationCode(@Valid @RequestBody EmailVerificationDto emailVerificationDto) {
        emailVerificationService.verifyEmailVerificationCode(emailVerificationDto);

        return ResponseEntity.ok("ok");
    }
}
