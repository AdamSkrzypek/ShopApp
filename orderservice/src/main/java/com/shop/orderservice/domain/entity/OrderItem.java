package com.shop.orderservice.domain.entity;



import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Getter
    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private UUID productId;

    @ManyToOne
    private Order order;

    @Builder
    public OrderItem(UUID productId, String productName, int quantity, BigDecimal unitPrice) {
        validateQuantity(quantity);
        validatePrice(unitPrice);
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.productName = Objects.requireNonNull(productName, "Product name cannot be null");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

}