// java
package com.shop.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.orderservice.domain.dto.*;
import com.shop.orderservice.domain.entity.Customer;
import com.shop.orderservice.domain.entity.Order;
import com.shop.orderservice.domain.entity.OutboxEvent;
import com.shop.orderservice.exception.OrderNotFoundException;
import com.shop.orderservice.repository.OrderRepository;
import com.shop.orderservice.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderStatusResponseDTO createOrderFromDTO(OrderRequestDTO orderRequestDTO) {
        Customer customer = customerService.findOrCreate(orderRequestDTO.customerDetailsRequest());
        Order order = Order.create(orderRequestDTO, customer);
        Order savedOrder = orderRepository.save(order);
        createAndSaveOrderCreatedEvent(savedOrder, orderRequestDTO);
        return buildOrderStatusResponse(savedOrder);
    }

    private void createAndSaveOrderCreatedEvent(Order order, OrderRequestDTO orderRequestDTO) {
        OrderKafkaRequestDTO kafkaRequest = new OrderKafkaRequestDTO(
                order.getCustomer().getCustomerId(),
                order.getId(),
                orderRequestDTO
        );
        try {
            String payload = objectMapper.writeValueAsString(kafkaRequest);
            OutboxEvent event = new OutboxEvent(
                    order.getId(),
                    "Order",
                    "OrderCreated",
                    payload
            );
            outboxEventRepository.save(event);
        }catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize order event payload for order id: " + order.getId(), e);
        }
    }

    public OrderResponseDTO getOrderById(UUID id) {
        Order order = findOrderById(id);
        return new OrderResponseDTO(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getPaymentStatus()
        );
    }

    @Transactional
    public void cancelOrder(UUID id) {
        Order order = findOrderById(id);
        order.cancel();
        orderRepository.save(order);
    }

    private Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id: %s doesn't exist", id)));
    }

    private OrderStatusResponseDTO buildOrderStatusResponse(Order savedOrder) {
        return new OrderStatusResponseDTO(
                savedOrder.getId(),
                savedOrder.getCustomer().getCustomerId(),
                savedOrder.getStatus()
        );
    }
}
