package com.eolma.payment.domain.service;

import com.eolma.payment.domain.model.Settlement;
import com.eolma.payment.domain.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;

    @Value("${payment.fee-rate}")
    private BigDecimal feeRate;

    public Settlement createSettlement(Long paymentId, Long sellerId, Long totalAmount) {
        Settlement settlement = Settlement.create(paymentId, sellerId, totalAmount, feeRate);
        return settlementRepository.save(settlement);
    }
}
