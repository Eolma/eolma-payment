package com.eolma.payment.infra;

import com.eolma.payment.application.usecase.ExpirePaymentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDeadlineScheduler {

    private final ExpirePaymentUseCase expirePaymentUseCase;

    @Scheduled(fixedDelay = 60_000)
    public void expireOverduePayments() {
        int count = expirePaymentUseCase.execute();
        if (count > 0) {
            log.info("Expired {} overdue payments", count);
        }
    }
}
