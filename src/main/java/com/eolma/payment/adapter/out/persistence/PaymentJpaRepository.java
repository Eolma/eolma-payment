package com.eolma.payment.adapter.out.persistence;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;
import com.eolma.payment.domain.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long>, PaymentRepository {

    @Override
    Optional<Payment> findByAuctionId(Long auctionId);

    @Override
    boolean existsByAuctionId(Long auctionId);

    @Override
    Optional<Payment> findByTossOrderId(String tossOrderId);

    @Override
    List<Payment> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    @Override
    List<Payment> findByStatusAndDeadlineAtBefore(PaymentStatus status, LocalDateTime deadline);
}
