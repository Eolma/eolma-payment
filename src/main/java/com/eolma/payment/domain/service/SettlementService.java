package com.eolma.payment.domain.service;

import com.eolma.payment.domain.model.Settlement;
import com.eolma.payment.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final BigDecimal feeRate;

    public Settlement createSettlement(Long paymentId, Long sellerId, Long totalAmount) {
        Settlement settlement = Settlement.create(paymentId, sellerId, totalAmount, feeRate);
        return settlementRepository.save(settlement);
    }
}
