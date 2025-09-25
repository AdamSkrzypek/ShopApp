package com.shop.orderservice.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(UUID productId, String productName, Integer quantity, BigDecimal unitPrice) {}
