package com.eolma.payment.adapter.in.web.dto;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;
import com.eolma.payment.domain.model.PaymentType;

import java.time.LocalDateTime;

public record PaymentResponse(
        String id,
        Long auctionId,
        Long productId,
        String buyerId,
        String sellerId,
        Long amount,
        PaymentType paymentType,
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
                payment.getPaymentType(),
                payment.getStatus(),
                payment.getTossOrderId(),
                payment.getPaymentMethod(),
                payment.getDeadlineAt(),
                payment.getConfirmedAt(),
                payment.getCreatedAt()
        );
    }
}
