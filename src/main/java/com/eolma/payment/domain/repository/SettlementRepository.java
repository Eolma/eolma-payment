package com.eolma.payment.domain.repository;

import com.eolma.payment.domain.model.Settlement;

import java.util.Optional;

public interface SettlementRepository {

    Settlement save(Settlement settlement);

    Optional<Settlement> findByPaymentId(String paymentId);
}
