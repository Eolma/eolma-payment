package com.eolma.payment.adapter.out.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class TossPaymentsConfig {

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    @Value("${toss.payments.base-url}")
    private String baseUrl;

    @Bean("tossRestTemplate")
    public RestTemplate tossRestTemplate(RestTemplateBuilder builder) {
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return builder
                .rootUri(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }
}
