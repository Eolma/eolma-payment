package com.eolma.payment.application.usecase;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.PaymentCancelledEvent;
import com.eolma.payment.application.port.out.EventPublisher;
import com.eolma.payment.application.port.out.PaymentGateway;
import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;
import com.eolma.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelPaymentUseCase {

    private final PaymentService paymentService;
    private final PaymentGateway paymentGateway;
    private final EventPublisher eventPublisher;

    @Transactional
    public Payment execute(Long paymentId, String cancelReason) {
        Payment payment = paymentService.findById(paymentId);

        if (payment.getStatus() == PaymentStatus.CONFIRMED && payment.getTossPaymentKey() != null) {
            paymentGateway.cancelPayment(payment.getTossPaymentKey(), cancelReason);
            payment.refund();
        } else {
            paymentService.validatePending(payment);
            payment.cancel();
        }

        log.info("Payment cancelled: paymentId={}, auctionId={}, status={}",
                payment.getId(), payment.getAuctionId(), payment.getStatus());

        DomainEvent<PaymentCancelledEvent> event = DomainEvent.create(
                EventType.PAYMENT_CANCELLED,
                "payment-service",
                String.valueOf(payment.getId()),
                "Payment",
                new PaymentCancelledEvent(
                        payment.getId(),
                        payment.getAuctionId(),
                        payment.getBuyerId(),
                        payment.getAmount(),
                        LocalDateTime.now()
                )
        );
        eventPublisher.publish(event);

        return payment;
    }
}
