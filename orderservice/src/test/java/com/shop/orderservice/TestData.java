package com.shop.orderservice;

import com.shop.orderservice.domain.dto.*;
import com.shop.orderservice.domain.entity.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestData {
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String EMAIL = "john@example.com";
    public static final String PHONE = "+48123456789";
    public static final String STREET = "Street 1";
    public static final String CITY = "City";
    public static final String POSTAL_CODE = "12-345";
    public static final String COUNTRY = "Country";
    public static final String PRODUCT_NAME = "Test Product";
    public static final int QUANTITY = 2;
    public static final BigDecimal UNIT_PRICE = BigDecimal.TEN;

    public static Customer createCustomer() {
        return new Customer(
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                PHONE,
                STREET,
                CITY,
                POSTAL_CODE,
                COUNTRY
        );
    }

    public static OrderRequestDTO createOrderRequestDTO() {
        return new OrderRequestDTO(
                createCustomerDetailsRequestDTO(),
                List.of(createOrderItemDTO())
        );
    }

    private static CustomerDetailsRequestDTO createCustomerDetailsRequestDTO() {
        return new CustomerDetailsRequestDTO(
                createPersonDTO(),
                createAddressDTO()
        );
    }

    private static PersonDTO createPersonDTO() {
        return new PersonDTO(
                FIRST_NAME,
                LAST_NAME,
                EMAIL,
                PHONE
        );
    }

    private static AddressDTO createAddressDTO() {
        return new AddressDTO(
                STREET,
                CITY,
                POSTAL_CODE,
                COUNTRY
        );
    }

    private static OrderItemDTO createOrderItemDTO() {
        return new OrderItemDTO(
                UUID.randomUUID(),
                PRODUCT_NAME,
                QUANTITY,
                UNIT_PRICE
        );
    }
}