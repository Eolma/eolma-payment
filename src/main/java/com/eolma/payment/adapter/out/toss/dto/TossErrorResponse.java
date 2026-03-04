package com.eolma.payment.adapter.out.toss.dto;

public record TossErrorResponse(
        String code,
        String message
) {
}
