package com.shop.orderservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.orderservice.controller.OrderController;
import com.shop.orderservice.domain.dto.OrderKafkaRequestDTO;
import com.shop.orderservice.domain.dto.OrderRequestDTO;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

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

    public void publishOrderRequest(UUID customerId, UUID orderId, OrderRequestDTO request) throws JsonProcessingException {
        OrderKafkaRequestDTO message = new OrderKafkaRequestDTO(customerId, orderId, request);
        String payload = objectMapper.writeValueAsString(message);
        kafkaTemplate.send(orderRequestTopic, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        RecordMetadata meta = result.getRecordMetadata();
                        logger.info("Sent message to topic={}, partition={}, offset={}",
                                meta.topic(), meta.partition(), meta.offset());
                    } else {
                        logger.error("Failed to send message", ex);
                    }
                });
    }
}
