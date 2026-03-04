package com.eolma.payment.application.port.out;

public record PaymentGatewayResult(
        String paymentKey,
        String method
) {
}
