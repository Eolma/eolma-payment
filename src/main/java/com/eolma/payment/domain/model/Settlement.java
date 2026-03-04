package com.eolma.payment.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long paymentId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal feeRate;

    @Column(nullable = false)
    private Long feeAmount;

    @Column(nullable = false)
    private Long settleAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementStatus status;

    private LocalDateTime settledAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static Settlement create(Long paymentId, Long sellerId, Long totalAmount, BigDecimal feeRate) {
        Settlement settlement = new Settlement();
        settlement.paymentId = paymentId;
        settlement.sellerId = sellerId;
        settlement.totalAmount = totalAmount;
        settlement.feeRate = feeRate;
        // 원 미만 절사
        settlement.feeAmount = BigDecimal.valueOf(totalAmount)
                .multiply(feeRate)
                .longValue();
        settlement.settleAmount = totalAmount - settlement.feeAmount;
        settlement.status = SettlementStatus.PENDING;
        return settlement;
    }

    public void settle() {
        this.status = SettlementStatus.SETTLED;
        this.settledAt = LocalDateTime.now();
    }
}
