package com.eolma.payment.application.usecase;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.PaymentConfirmedEvent;
import com.eolma.payment.application.port.out.EventPublisher;
import com.eolma.payment.application.port.out.PaymentGateway;
import com.eolma.payment.application.port.out.PaymentGatewayResult;
import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.service.PaymentService;
import com.eolma.payment.domain.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfirmPaymentUseCase {

    private final PaymentService paymentService;
    private final SettlementService settlementService;
    private final PaymentGateway paymentGateway;
    private final EventPublisher eventPublisher;

    @Transactional
    public Payment execute(String paymentKey, String orderId, Long amount) {
        Payment payment = paymentService.findByTossOrderId(orderId);

        paymentService.validatePending(payment);
        paymentService.validateAmount(payment, amount);

        PaymentGatewayResult result = paymentGateway.confirmPayment(paymentKey, orderId, amount);

        payment.confirm(paymentKey, result.method());

        settlementService.createSettlement(payment.getId(), payment.getSellerId(), payment.getAmount());

        log.info("Payment confirmed: paymentId={}, auctionId={}, amount={}",
                payment.getId(), payment.getAuctionId(), payment.getAmount());

        DomainEvent<PaymentConfirmedEvent> event = DomainEvent.create(
                EventType.PAYMENT_CONFIRMED,
                "payment-service",
                String.valueOf(payment.getId()),
                "Payment",
                new PaymentConfirmedEvent(
                        payment.getId(),
                        payment.getAuctionId(),
                        payment.getProductId(),
                        payment.getBuyerId(),
                        payment.getSellerId(),
                        payment.getAmount(),
                        paymentKey,
                        LocalDateTime.now()
                )
        );
        eventPublisher.publish(event);

        return payment;
    }
}
