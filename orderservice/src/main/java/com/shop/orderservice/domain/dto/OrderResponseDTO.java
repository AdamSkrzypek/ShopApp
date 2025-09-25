package com.shop.orderservice.domain.dto;

import com.shop.orderservice.domain.enums.OrderStatus;
import com.shop.orderservice.domain.enums.PaymentStatus;
import com.shop.orderservice.domain.enums.ShippingStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDTO(UUID orderId, OrderStatus orderStatus, LocalDateTime createdAt,
                               PaymentStatus paymentStatus) {
}
