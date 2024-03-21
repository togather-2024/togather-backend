package com.togather.partyroom.payment.repository;

import com.togather.partyroom.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select p from Payment p where p.orderId = :orderId")
    Optional<Payment> findByOrderId(String orderId);

}
