package com.shop.orderservice.kafka;

import com.shop.orderservice.domain.dto.OrderResponseDTO;
import com.shop.orderservice.domain.entity.Order;
import com.shop.orderservice.exception.OrderNotFoundException;
import com.shop.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class KafkaEventListener {

    private final OrderRepository orderRepository;

    public KafkaEventListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    @KafkaListener(topics = "${topics.order.response}", groupId = "group.id")
    public void handleOrderResponse(OrderResponseDTO dto) {
        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException("Order with id: " + dto.orderId() + " doesn't exist"));
        order.setStatus(dto.orderStatus());
        orderRepository.save(order);
    }
}
