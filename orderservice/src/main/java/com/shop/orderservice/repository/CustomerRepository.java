package com.shop.orderservice.repository;


import com.shop.orderservice.domain.entity.Customer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

   Optional< Customer> findCustomerByEmail(String email);

}
