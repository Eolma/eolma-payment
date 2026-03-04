package com.eolma.payment.domain.repository;

import com.eolma.payment.domain.model.ProcessedEvent;

public interface ProcessedEventRepository {

    boolean existsByEventId(String eventId);

    ProcessedEvent save(ProcessedEvent processedEvent);
}
