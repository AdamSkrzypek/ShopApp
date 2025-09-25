package com.shop.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.orderservice.domain.dto.*;
import com.shop.orderservice.domain.entity.Customer;
import com.shop.orderservice.domain.entity.Order;
import com.shop.orderservice.exception.KafkaPublishingException;
import com.shop.orderservice.exception.OrderNotFoundException;
import com.shop.orderservice.kafka.KafkaOrderPublisher;
import com.shop.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final KafkaOrderPublisher kafkaOrderPublisher;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        CustomerService customerService,
                        KafkaOrderPublisher kafkaOrderPublisher) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.kafkaOrderPublisher = kafkaOrderPublisher;
    }

    public OrderStatusResponseDTO createOrderFromDTO(OrderRequestDTO orderRequestDTO) {
        Customer customer = customerService.findOrCreate(orderRequestDTO.customerDetailsRequest());
        Order order = Order.create(orderRequestDTO, customer);
        Order savedOrder = orderRepository.save(order);
        publishToKafka(savedOrder, orderRequestDTO);
        return buildOrderStatusResponse(savedOrder);
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
    public void cancelOrder(UUID id) {
        Order order = findOrderById(id);
        order.cancel();
        orderRepository.save(order);
    }

    private Order findOrderById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id: " + id + " doesn't exist"));
    }

    private OrderStatusResponseDTO buildOrderStatusResponse(Order savedOrder) {
        return new OrderStatusResponseDTO(
                savedOrder.getId(),
                savedOrder.getCustomer().getCustomerId(),
                savedOrder.getStatus()
        );
    }


    public void publishToKafka(Order savedOrder, OrderRequestDTO orderRequestDTO) {
        try {
            kafkaOrderPublisher.publishOrderRequest(
                    savedOrder.getCustomer().getCustomerId(),
                    savedOrder.getId(),
                    orderRequestDTO
            );
        } catch (JsonProcessingException e) {
            logger.error("Failed to publish order to Kafka", e);
            throw new KafkaPublishingException("Kafka publishing failed", e);
        }
    }
}

