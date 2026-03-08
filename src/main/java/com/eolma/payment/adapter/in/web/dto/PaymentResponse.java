package com.eolma.payment.adapter.in.web.dto;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        Long auctionId,
        Long productId,
        String buyerId,
        String sellerId,
        Long amount,
        PaymentStatus status,
        String tossOrderId,
        String paymentMethod,
        LocalDateTime deadlineAt,
        LocalDateTime confirmedAt,
        LocalDateTime createdAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAuctionId(),
                payment.getProductId(),
                payment.getBuyerId(),
                payment.getSellerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTossOrderId(),
                payment.getPaymentMethod(),
                payment.getDeadlineAt(),
                payment.getConfirmedAt(),
                payment.getCreatedAt()
        );
    }
}
