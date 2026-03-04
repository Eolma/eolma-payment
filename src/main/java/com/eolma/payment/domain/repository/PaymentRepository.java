package com.eolma.payment.domain.repository;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByAuctionId(Long auctionId);

    boolean existsByAuctionId(Long auctionId);

    Optional<Payment> findByTossOrderId(String tossOrderId);

    List<Payment> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    List<Payment> findByStatusAndDeadlineAtBefore(PaymentStatus status, LocalDateTime deadline);
}
