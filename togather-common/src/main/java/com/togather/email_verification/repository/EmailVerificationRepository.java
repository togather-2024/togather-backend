package com.togather.email_verification.repository;

import com.togather.email_verification.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    @Query("SELECT e FROM EmailVerification e WHERE e.receiverEmailAddress = :email ORDER BY e.emailVerificationId DESC LIMIT 1")
    Optional<EmailVerification> findLatestByEmail(String email);
}
