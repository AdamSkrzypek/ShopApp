package com.shop.orderservice.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(@NotNull CustomerDetailsRequestDTO customerDetailsRequest,
                              @NotNull List<OrderItemDTO> orderItems)  {}
