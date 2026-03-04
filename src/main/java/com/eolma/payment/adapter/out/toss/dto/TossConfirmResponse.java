package com.eolma.payment.adapter.out.toss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,
        String method,
        Long totalAmount,
        String approvedAt
) {
}
