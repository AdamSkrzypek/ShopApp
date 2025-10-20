package com.shop.orderservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.orderservice.domain.dto.OrderKafkaRequestDTO;
import com.shop.orderservice.domain.dto.OrderRequestDTO;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaOrderPublisher {
    private Logger logger = LoggerFactory.getLogger(KafkaOrderPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    @Value("${topics.order.request}")
    private String orderRequestTopic;

    public KafkaOrderPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;

    }


    /**
     * Publishes a message to Kafka synchronously. This method blocks until the broker
     * acknowledges the message, throwing an exception on failure.
     * <p>
     * Use this for critical events where delivery must be guaranteed before proceeding,
     * such as within the Outbox Processor.
     *
     * @param key     The message key.
     * @param payload The message payload.
     * @throws RuntimeException if publishing fails.
     */
    public void publishSync(String key, String payload) {
        try {
            kafkaTemplate.send(orderRequestTopic, key, payload).get();
            logger.info("Successfully sent message synchronously for key {}", key);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Failed to send message synchronously for key {}", key, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to send message synchronously to Kafka", e);
        }
    }

    /**
     * Publishes a message to Kafka asynchronously ("fire-and-forget"). This method returns
     * immediately and handles the result in a callback.
     * <p>
     * Use this for non-critical events where maximum performance is desired and
     * occasional message loss during an application crash is acceptable (e.g., logging, metrics).
     *
     * @param key     The message key.
     * @param payload The message payload.
     */
    public void publishAsync(String key, String payload) {
        kafkaTemplate.send(orderRequestTopic, key, payload).whenComplete((result, ex) -> {
            if (ex == null) {
                RecordMetadata meta = result.getRecordMetadata();
                logger.info("Sent message asynchronously to topic={}, partition={}, offset={}",
                        meta.topic(), meta.partition(), meta.offset());
            } else {
                logger.error("Failed to send message asynchronously for key {}", key, ex);
            }
        });
    }
}
