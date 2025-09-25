package com.shop.orderservice.domain.dto;

import java.util.UUID;

public record OrderKafkaRequestDTO(UUID customerId, UUID orderId, OrderRequestDTO request) {
}
