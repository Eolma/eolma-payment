package com.eolma.payment.domain.service;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import com.eolma.payment.domain.model.Payment;
import com.eolma.payment.domain.model.PaymentStatus;
import com.eolma.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class PaymentService {

    private static final DateTimeFormatter ORDER_ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PaymentRepository paymentRepository;

    public Payment createPayment(Long auctionId, Long productId, String buyerId, String sellerId,
                                  Long amount, int deadlineHours) {
        return createPayment(auctionId, productId, buyerId, sellerId, amount,
                LocalDateTime.now().plusHours(deadlineHours));
    }

    public Payment createPayment(Long auctionId, Long productId, String buyerId, String sellerId,
                                  Long amount, LocalDateTime deadline) {
        return paymentRepository.findByAuctionId(auctionId)
                .orElseGet(() -> {
                    String orderId = generateOrderId(auctionId);
                    Payment payment = Payment.builder()
                            .auctionId(auctionId)
                            .productId(productId)
                            .buyerId(buyerId)
                            .sellerId(sellerId)
                            .amount(amount)
                            .tossOrderId(orderId)
                            .deadlineAt(deadline)
                            .build();
                    return paymentRepository.save(payment);
                });
    }

    public Payment findById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EolmaException(ErrorType.PAYMENT_NOT_FOUND,
                        "Payment not found: " + id));
    }

    public Payment findByAuctionId(Long auctionId) {
        return paymentRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new EolmaException(ErrorType.PAYMENT_NOT_FOUND,
                        "Payment not found for auction: " + auctionId));
    }

    public Payment findByTossOrderId(String orderId) {
        return paymentRepository.findByTossOrderId(orderId)
                .orElseThrow(() -> new EolmaException(ErrorType.PAYMENT_NOT_FOUND,
                        "Payment not found for orderId: " + orderId));
    }

    public List<Payment> findByBuyerId(String buyerId) {
        return paymentRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    public List<Payment> findExpiredPayments() {
        return paymentRepository.findByStatusAndDeadlineAtBefore(
                PaymentStatus.PENDING, LocalDateTime.now());
    }

    public void validateAmount(Payment payment, Long requestAmount) {
        if (!payment.getAmount().equals(requestAmount)) {
            throw new EolmaException(ErrorType.PAYMENT_AMOUNT_MISMATCH,
                    "Amount mismatch: expected=" + payment.getAmount() + ", actual=" + requestAmount);
        }
    }

    public void validatePending(Payment payment) {
        if (!payment.isPending()) {
            throw new EolmaException(ErrorType.PAYMENT_NOT_PENDING,
                    "Payment is not in PENDING status: " + payment.getId());
        }
        if (payment.isExpired()) {
            throw new EolmaException(ErrorType.PAYMENT_EXPIRED,
                    "Payment deadline has passed: " + payment.getId());
        }
    }

    private String generateOrderId(Long auctionId) {
        return "EOLMA_" + auctionId + "_" + LocalDateTime.now().format(ORDER_ID_FORMAT);
    }
}
