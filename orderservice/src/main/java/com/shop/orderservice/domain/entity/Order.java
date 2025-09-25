package com.shop.orderservice.domain.entity;

import com.shop.orderservice.domain.dto.OrderRequestDTO;
import com.shop.orderservice.domain.enums.OrderStatus;
import com.shop.orderservice.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @Setter
    private Customer customer;

    @Embedded
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Builder
    private Order(Customer customer, ShippingAddress shippingAddress, List<OrderItem> orderItems) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.orderItems.addAll(orderItems);
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.NEW;
        this.paymentStatus = PaymentStatus.NEW;
        orderItems.forEach(item -> item.setOrder(this));
    }

    public static Order create(OrderRequestDTO dto, Customer customer) {
        return Order.builder()
                .customer(customer)
                .shippingAddress(ShippingAddress.fromDto(dto.customerDetailsRequest().addressDTO()))
                .orderItems(dto.orderItems().stream()
                        .map(itemDTO -> new OrderItem(
                                itemDTO.productId(),
                                itemDTO.productName(),
                                itemDTO.quantity(),
                                itemDTO.unitPrice()))
                        .toList())
                .build();
    }

    public void cancel() {
        if (!this.status.equals(OrderStatus.NEW)) {
            throw new IllegalStateException("Only NEW orders can be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(orderItems);
    }
}

