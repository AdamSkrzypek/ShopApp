package com.shop.orderservice.domain.dto;

import com.shop.orderservice.domain.enums.OrderStatus;

import java.util.UUID;

public record OrderStatusResponseDTO(UUID orderId, UUID customerId, OrderStatus orderStatus) {
}
