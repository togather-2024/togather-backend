package com.togather.emailVerification;

import com.togather.email_verification.dto.EmailVerificationDto;
import com.togather.email_verification.service.EmailVerificationService;
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
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping()
    public ResponseEntity<String> sendEmailVerification(@Valid @RequestBody EmailVerificationDto emailVerificationDto) {
        emailVerificationService.registerEmailVerification(emailVerificationDto);

        return ResponseEntity.ok("ok");
    }

    @PostMapping("/validity")
    public ResponseEntity<String> verifyEmailVerificationCode(@Valid @RequestBody EmailVerificationDto emailVerificationDto) {
        emailVerificationService.verifyEmailVerificationCode(emailVerificationDto);

        return ResponseEntity.ok("ok");
    }
}
