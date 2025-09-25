package com.shop.orderservice.controller;

import com.shop.orderservice.domain.dto.OrderRequestDTO;
import com.shop.orderservice.domain.dto.OrderResponseDTO;
import com.shop.orderservice.domain.dto.OrderStatusResponseDTO;
import com.shop.orderservice.service.OrderService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  com.shop.orderservice.generated.*;

import java.util.UUID;

@RestController
@RequestMapping("v1/api/orders")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;


    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderStatusResponseDTO> createNewOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        logger.info("Processing incoming order request for customer: {} and items: {} ",
                orderRequestDTO.customerDetailsRequest().personDTO(), orderRequestDTO.orderItems().size());
        return ResponseEntity.ok(orderService.createOrderFromDTO(orderRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        if (order != null) {
            logger.info("Found order with id: {} ", id);
            return ResponseEntity.ok(order);
        }
        logger.info("Cannot find order with id: {} ", id);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
        logger.info("Deleting order with id: {} ", id);
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
