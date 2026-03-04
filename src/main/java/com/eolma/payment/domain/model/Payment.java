package com.eolma.payment.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long auctionId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long buyerId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 200)
    private String tossPaymentKey;

    @Column(nullable = false, unique = true, length = 100)
    private String tossOrderId;

    @Column(length = 30)
    private String paymentMethod;

    private LocalDateTime confirmedAt;

    private LocalDateTime cancelledAt;

    @Column(length = 500)
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime deadlineAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Payment(Long auctionId, Long productId, Long buyerId, Long sellerId,
                   Long amount, String tossOrderId, LocalDateTime deadlineAt) {
        this.auctionId = auctionId;
        this.productId = productId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.tossOrderId = tossOrderId;
        this.deadlineAt = deadlineAt;
    }

    public void confirm(String tossPaymentKey, String paymentMethod) {
        this.status = PaymentStatus.CONFIRMED;
        this.tossPaymentKey = tossPaymentKey;
        this.paymentMethod = paymentMethod;
        this.confirmedAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = PaymentStatus.EXPIRED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.failureReason = reason;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    public boolean isExpired() {
        return this.deadlineAt.isBefore(LocalDateTime.now());
    }
}
