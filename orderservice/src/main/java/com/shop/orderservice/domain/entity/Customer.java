package com.shop.orderservice.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "customers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID customerId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String street;
    private String city;
    private String postalCode;
    private String country;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> customerOrders = new HashSet<>();

    @Builder
    public Customer(String firstName, String lastName, String email, String phoneNumber,
                    String street, String city, String postalCode, String country) {
        this.firstName = Objects.requireNonNull(firstName, "First name cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "Last name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
        this.street = Objects.requireNonNull(street, "Street cannot be null");
        this.city = Objects.requireNonNull(city, "City cannot be null");
        this.postalCode = Objects.requireNonNull(postalCode, "Postal code cannot be null");
        this.country = Objects.requireNonNull(country, "Country cannot be null");
    }

    public void addOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        customerOrders.add(order);
        order.setCustomer(this);
    }

}