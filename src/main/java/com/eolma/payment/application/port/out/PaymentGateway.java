package com.eolma.payment.application.port.out;

public interface PaymentGateway {

    PaymentGatewayResult confirmPayment(String paymentKey, String orderId, Long amount);

    void cancelPayment(String paymentKey, String cancelReason);
}
