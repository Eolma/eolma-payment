package com.eolma.payment.adapter.out.toss;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import com.eolma.payment.adapter.out.toss.dto.TossConfirmRequest;
import com.eolma.payment.adapter.out.toss.dto.TossConfirmResponse;
import com.eolma.payment.adapter.out.toss.dto.TossErrorResponse;
import com.eolma.payment.application.port.out.PaymentGateway;
import com.eolma.payment.application.port.out.PaymentGatewayResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
public class TossPaymentsClient implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TossPaymentsClient(@Qualifier("tossRestTemplate") RestTemplate restTemplate,
                               ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public PaymentGatewayResult confirmPayment(String paymentKey, String orderId, Long amount) {
        TossConfirmRequest request = new TossConfirmRequest(paymentKey, orderId, amount);

        try {
            ResponseEntity<TossConfirmResponse> response = restTemplate.postForEntity(
                    "/payments/confirm", request, TossConfirmResponse.class);

            log.info("Toss payment confirmed: paymentKey={}, orderId={}", paymentKey, orderId);

            TossConfirmResponse body = response.getBody();
            return new PaymentGatewayResult(body.paymentKey(), body.method());

        } catch (HttpClientErrorException e) {
            TossErrorResponse error = parseError(e.getResponseBodyAsString());
            log.error("Toss payment confirm failed: code={}, message={}, paymentKey={}",
                    error.code(), error.message(), paymentKey);
            throw new EolmaException(ErrorType.PAYMENT_FAILED, error.message());
        }
    }

    @Override
    public void cancelPayment(String paymentKey, String cancelReason) {
        try {
            restTemplate.postForEntity(
                    "/payments/{paymentKey}/cancel",
                    Map.of("cancelReason", cancelReason),
                    String.class,
                    paymentKey);

            log.info("Toss payment cancelled: paymentKey={}", paymentKey);

        } catch (HttpClientErrorException e) {
            TossErrorResponse error = parseError(e.getResponseBodyAsString());
            log.error("Toss payment cancel failed: code={}, message={}, paymentKey={}",
                    error.code(), error.message(), paymentKey);
            throw new EolmaException(ErrorType.PAYMENT_FAILED, error.message());
        }
    }

    private TossErrorResponse parseError(String body) {
        try {
            return objectMapper.readValue(body, TossErrorResponse.class);
        } catch (Exception e) {
            return new TossErrorResponse("UNKNOWN", "Failed to parse error response");
        }
    }
}
