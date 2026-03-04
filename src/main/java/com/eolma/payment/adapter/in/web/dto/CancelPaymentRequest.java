package com.eolma.payment.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentRequest(
        @NotBlank String cancelReason
) {
}
