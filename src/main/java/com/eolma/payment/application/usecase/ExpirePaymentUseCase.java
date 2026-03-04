package com.eolma.payment.application.usecase;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.PaymentExpiredEvent;
import com.eolma.payment.application.port.out.EventPublisher;
import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirePaymentUseCase {

    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;

    public int execute() {
        List<Payment> expiredPayments = paymentService.findExpiredPayments();
        int count = 0;

        for (Payment payment : expiredPayments) {
            try {
                expireSingle(payment);
                count++;
            } catch (Exception e) {
                log.error("Failed to expire payment: paymentId={}", payment.getId(), e);
            }
        }

        return count;
    }

    @Transactional
    public void expireSingle(Payment payment) {
        payment.expire();

        log.info("Payment expired: paymentId={}, auctionId={}",
                payment.getId(), payment.getAuctionId());

        DomainEvent<PaymentExpiredEvent> event = DomainEvent.create(
                EventType.PAYMENT_EXPIRED,
                "payment-service",
                String.valueOf(payment.getId()),
                "Payment",
                new PaymentExpiredEvent(
                        payment.getId(),
                        payment.getAuctionId(),
                        payment.getBuyerId(),
                        payment.getAmount(),
                        LocalDateTime.now()
                )
        );
        eventPublisher.publish(event);
    }
}
