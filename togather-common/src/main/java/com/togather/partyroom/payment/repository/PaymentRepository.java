package com.togather.partyroom.payment.repository;

import com.togather.partyroom.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Long, Payment> {
}
