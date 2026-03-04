package com.eolma.payment.adapter.in.web;

import com.eolma.payment.adapter.in.web.dto.CancelPaymentRequest;
import com.eolma.payment.adapter.in.web.dto.ConfirmPaymentRequest;
import com.eolma.payment.adapter.in.web.dto.PaymentResponse;
import com.eolma.payment.application.usecase.CancelPaymentUseCase;
import com.eolma.payment.application.usecase.ConfirmPaymentUseCase;
import com.eolma.payment.application.usecase.GetPaymentUseCase;
import com.eolma.payment.domain.model.Payment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ConfirmPaymentUseCase confirmPaymentUseCase;
    private final CancelPaymentUseCase cancelPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<PaymentResponse> findByAuctionId(@PathVariable Long auctionId) {
        Payment payment = getPaymentUseCase.findByAuctionId(auctionId);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirm(@Valid @RequestBody ConfirmPaymentRequest request) {
        Payment payment = confirmPaymentUseCase.execute(
                request.paymentKey(), request.orderId(), request.amount());
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancel(@PathVariable Long id,
                                                   @Valid @RequestBody CancelPaymentRequest request) {
        Payment payment = cancelPaymentUseCase.execute(id, request.cancelReason());
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> findMyPayments(
            @RequestHeader("X-User-Id") Long buyerId) {
        List<Payment> payments = getPaymentUseCase.findByBuyerId(buyerId);
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable Long id) {
        Payment payment = getPaymentUseCase.findById(id);
        return ResponseEntity.ok(PaymentResponse.from(payment));
    }
}
