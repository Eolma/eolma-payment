package com.eolma.payment.application.usecase;

import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetPaymentUseCase {

    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public Payment findById(Long id) {
        return paymentService.findById(id);
    }

    @Transactional(readOnly = true)
    public Payment findByAuctionId(Long auctionId) {
        return paymentService.findByAuctionId(auctionId);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByBuyerId(Long buyerId) {
        return paymentService.findByBuyerId(buyerId);
    }
}
