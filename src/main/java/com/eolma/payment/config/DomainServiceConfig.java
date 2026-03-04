package com.eolma.payment.config;

import com.eolma.payment.domain.repository.PaymentRepository;
import com.eolma.payment.domain.repository.SettlementRepository;
import com.eolma.payment.domain.service.PaymentService;
import com.eolma.payment.domain.service.SettlementService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DomainServiceConfig {

    @Bean
    public PaymentService paymentService(PaymentRepository paymentRepository) {
        return new PaymentService(paymentRepository);
    }

    @Bean
    public SettlementService settlementService(SettlementRepository settlementRepository,
                                                @Value("${payment.fee-rate}") BigDecimal feeRate) {
        return new SettlementService(settlementRepository, feeRate);
    }
}
