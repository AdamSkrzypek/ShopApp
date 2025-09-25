package com.shop.orderservice;

import com.shop.orderservice.controller.OrderController;
import com.shop.orderservice.domain.dto.*;
import com.shop.orderservice.domain.enums.OrderStatus;
import com.shop.orderservice.domain.enums.PaymentStatus;
import com.shop.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class OrderControllerUnitTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID orderId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    @Test
    void createNewOrder_shouldReturnOrderStatusResponseDTO() {
        // given
        OrderRequestDTO request = new OrderRequestDTO(
                new CustomerDetailsRequestDTO(
                        new PersonDTO("Jan", "Kowalski", "jan.kowalski@example.com", "+48 123 456 789"),
                        new AddressDTO("ul. Kwiatowa 5", "Warszawa", "00-001", "Polska")
                ),
                List.of(new OrderItemDTO(
                        UUID.randomUUID(),
                        "Produkt testowy",
                        2,
                        new BigDecimal("19.99"))
                ));

        OrderStatusResponseDTO responseDTO = new OrderStatusResponseDTO(orderId, customerId,OrderStatus.PENDING);
        when(orderService.createOrderFromDTO(request)).thenReturn(responseDTO);

        // when
        ResponseEntity<OrderStatusResponseDTO> response = orderController.createNewOrder(request);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.PENDING, response.getBody().orderStatus());
        assertEquals(customerId, response.getBody().customerId());
    }

    @Test
    void getOrderById_shouldReturnOrder_whenExists() {
        OrderResponseDTO order = new OrderResponseDTO(
                orderId,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                PaymentStatus.NOT_PAID

        );

        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderId, response.getBody().orderId());
        assertEquals(OrderStatus.PENDING, response.getBody().orderStatus());
    }

    @Test
    void getOrderById_shouldReturnNotFound_whenOrderIsNull() {
        when(orderService.getOrderById(orderId)).thenReturn(null);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void cancelOrder_shouldReturnNoContent() {
        doNothing().when(orderService).cancelOrder(orderId);

        ResponseEntity<Void> response = orderController.cancelOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService, times(1)).cancelOrder(orderId);
    }
}
