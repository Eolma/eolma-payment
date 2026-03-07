package com.eolma.payment.adapter.in.kafka;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.event.EventType;
import com.eolma.common.event.payload.AuctionCompletedEvent;
import com.eolma.common.event.payload.InstantBuyStartedEvent;
import com.eolma.payment.application.usecase.CreatePaymentUseCase;
import com.eolma.payment.domain.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionEventConsumer {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "eolma.auction.events",
            groupId = "payment-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, DomainEvent<?>> record, Acknowledgment ack) {
        DomainEvent<?> event = record.value();

        try {
            if (processedEventRepository.existsByEventId(event.id())) {
                log.debug("Duplicate event skipped: eventId={}", event.id());
                ack.acknowledge();
                return;
            }

            switch (event.type()) {
                case EventType.AUCTION_COMPLETED -> handleAuctionCompleted(event);
                case EventType.INSTANT_BUY_STARTED -> handleInstantBuyStarted(event);
                default -> { }
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process auction event: eventId={}, type={}", event.id(), event.type(), e);
        }
    }

    private void handleAuctionCompleted(DomainEvent<?> event) {
        AuctionCompletedEvent payload = objectMapper.convertValue(
                event.payload(), AuctionCompletedEvent.class);

        createPaymentUseCase.execute(
                event.id(),
                payload.auctionId(),
                payload.productId(),
                payload.winnerId(),
                payload.sellerId(),
                payload.finalPrice()
        );

        log.info("Auction completed event processed: auctionId={}, winnerId={}",
                payload.auctionId(), payload.winnerId());
    }

    private void handleInstantBuyStarted(DomainEvent<?> event) {
        InstantBuyStartedEvent payload = objectMapper.convertValue(
                event.payload(), InstantBuyStartedEvent.class);

        createPaymentUseCase.executeWithDeadline(
                event.id(),
                payload.auctionId(),
                payload.productId(),
                payload.buyerId(),
                payload.sellerId(),
                payload.price(),
                payload.expiresAt()
        );

        log.info("Instant buy started event processed: auctionId={}, buyerId={}",
                payload.auctionId(), payload.buyerId());
    }
}
