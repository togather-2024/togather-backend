package com.togather.email_verification.repository;

import com.togather.email_verification.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

}
