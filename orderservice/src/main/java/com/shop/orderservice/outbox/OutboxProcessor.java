package com.shop.orderservice.outbox;

import com.shop.orderservice.domain.entity.OutboxEvent;
import com.shop.orderservice.kafka.KafkaOrderPublisher;
import com.shop.orderservice.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class OutboxProcessor {

    private final static Logger logger = LoggerFactory.getLogger(OutboxProcessor.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaOrderPublisher kafkaOrderPublisher;

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> eventsToProcess = outboxEventRepository.findAndLockOldestEventsToProcess();
        if (!eventsToProcess.isEmpty()){
            logger.info("Found {} events in outbox to process.", eventsToProcess.size());
        }
        for (OutboxEvent event : eventsToProcess) {
            try {

                kafkaOrderPublisher.publishSync(event.getAggregateId().toString(), event.getPayload());
                outboxEventRepository.delete(event);
                logger.info("Successfully processed and deleted outbox event {}", event.getEventId());
            }catch (Exception e){
                logger.error("Failed to process outbox event with id: {}", event.getEventId(), e);
            }

        }

    }
}
