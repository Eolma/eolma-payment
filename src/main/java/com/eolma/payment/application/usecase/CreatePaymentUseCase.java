package com.eolma.payment.application.usecase;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.ProcessedEvent;
import com.eolma.payment.domain.repository.ProcessedEventRepository;
import com.eolma.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;

    @Value("${payment.deadline-hours}")
    private int deadlineHours;

    @Transactional
    public Payment execute(String eventId, Long auctionId, Long productId,
                           Long buyerId, Long sellerId, Long amount) {
        Payment payment = paymentService.createPayment(
                auctionId, productId, buyerId, sellerId, amount, deadlineHours);

        processedEventRepository.save(new ProcessedEvent(eventId));

        log.info("Payment created: paymentId={}, auctionId={}, buyerId={}, amount={}",
                payment.getId(), auctionId, buyerId, amount);

        return payment;
    }
}
