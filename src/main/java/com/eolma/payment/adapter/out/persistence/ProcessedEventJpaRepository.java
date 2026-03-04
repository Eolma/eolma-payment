package com.eolma.payment.adapter.out.persistence;

import com.eolma.payment.domain.model.ProcessedEvent;
import com.eolma.payment.domain.repository.ProcessedEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, String>, ProcessedEventRepository {

    @Override
    default boolean existsByEventId(String eventId) {
        return existsById(eventId);
    }
}
