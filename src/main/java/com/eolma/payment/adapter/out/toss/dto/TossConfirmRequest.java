package com.eolma.payment.adapter.out.toss.dto;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
