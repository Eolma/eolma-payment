package com.eolma.payment.application.usecase;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentType;
import com.eolma.payment.domain.model.ProcessedEvent;
import com.eolma.payment.domain.repository.ProcessedEventRepository;
import com.eolma.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
                           String buyerId, String sellerId, Long amount) {
        return executeInternal(eventId, auctionId, productId, buyerId, sellerId, amount,
                PaymentType.AUCTION_WIN, LocalDateTime.now().plusHours(deadlineHours));
    }

    @Transactional
    public Payment executeWithDeadline(String eventId, Long auctionId, Long productId,
                                        String buyerId, String sellerId, Long amount,
                                        LocalDateTime deadline) {
        return executeInternal(eventId, auctionId, productId, buyerId, sellerId, amount,
                PaymentType.INSTANT_BUY, deadline);
    }

    private Payment executeInternal(String eventId, Long auctionId, Long productId,
                                     String buyerId, String sellerId, Long amount,
                                     PaymentType paymentType, LocalDateTime deadline) {
        Payment payment = paymentService.createPayment(
                auctionId, productId, buyerId, sellerId, amount, paymentType, deadline);

        processedEventRepository.save(new ProcessedEvent(eventId));

        log.info("Payment created: paymentId={}, auctionId={}, buyerId={}, amount={}, type={}, deadline={}",
                payment.getId(), auctionId, buyerId, amount, paymentType, deadline);

        return payment;
    }
}
