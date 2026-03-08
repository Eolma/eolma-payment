package com.eolma.payment.adapter.out.persistence;

import com.eolma.payment.domain.model.Settlement;
import com.eolma.payment.domain.repository.SettlementRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettlementJpaRepository extends JpaRepository<Settlement, String>, SettlementRepository {

    @Override
    Optional<Settlement> findByPaymentId(String paymentId);
}
