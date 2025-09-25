package com.shop.orderservice;

import com.shop.orderservice.domain.entity.Customer;
import com.shop.orderservice.domain.entity.Order;
import com.shop.orderservice.domain.entity.OrderItem;
import com.shop.orderservice.domain.enums.OrderStatus;
import com.shop.orderservice.repository.CustomerRepository;
import com.shop.orderservice.repository.OrderItemRepository;
import com.shop.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
class OrderJpaTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("shop_db")
            .withUsername("postgres")
            .withPassword("admin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer customer;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(TestData.createCustomer());
        order = Order.create(TestData.createOrderRequestDTO(), customer);
    }

    @Test
    @DisplayName("Should save order with customer and items")
    void shouldSaveOrderWithCustomerAndItems() {
        // when
        Order savedOrder = orderRepository.save(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getCustomer()).isEqualTo(customer);
        assertThat(savedOrder.getItems()).hasSize(1);
        assertThat(savedOrder.getItems().get(0).getProductName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("Should cascade delete order items when deleting order")
    void shouldCascadeDeleteOrderItemsWhenDeletingOrder() {
        // given
        Order savedOrder = orderRepository.save(order);
        UUID orderItemId = savedOrder.getItems().get(0).getProductId();

        // when
        orderRepository.delete(savedOrder);

        // then
        assertThat(orderRepository.existsById(savedOrder.getId())).isFalse();
        assertThat(orderItemRepository.existsById(orderItemId)).isFalse();
    }

    @Test
    @DisplayName("Should not delete customer when deleting order")
    void shouldNotDeleteCustomerWhenDeletingOrder() {
        // given
        Order savedOrder = orderRepository.save(order);

        // when
        orderRepository.delete(savedOrder);

        // then
        assertThat(customerRepository.existsById(customer.getCustomerId())).isTrue();
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        // given
        Order savedOrder = orderRepository.save(order);

        // when
        savedOrder.setStatus(OrderStatus.APPROVED);
        Order updatedOrder = orderRepository.save(savedOrder);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.APPROVED);
    }
}