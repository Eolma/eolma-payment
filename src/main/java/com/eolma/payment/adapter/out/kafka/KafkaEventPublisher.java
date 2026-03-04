package com.eolma.payment.adapter.out.kafka;

import com.eolma.common.event.DomainEvent;
import com.eolma.common.kafka.EolmaKafkaProducer;
import com.eolma.payment.application.port.out.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private static final String TOPIC = "eolma.payment.events";

    private final EolmaKafkaProducer kafkaProducer;

    @Override
    public void publish(DomainEvent<?> event) {
        kafkaProducer.publish(TOPIC, event);
    }
}
