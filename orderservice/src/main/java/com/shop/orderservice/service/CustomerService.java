package com.shop.orderservice.service;

import com.shop.orderservice.domain.dto.AddressDTO;
import com.shop.orderservice.domain.dto.CustomerDetailsRequestDTO;
import com.shop.orderservice.domain.dto.PersonDTO;
import com.shop.orderservice.domain.entity.Customer;
import com.shop.orderservice.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.function.Supplier;

@Service
public class CustomerService {
    private Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findOrCreate(CustomerDetailsRequestDTO dto) {
        logger.info("Searching for user by firstName and lastName");
        return customerRepository.findCustomerByFirstNameAndLastName(dto.personDTO().firstName(), dto.personDTO().lastName())
                .orElseGet(() -> ifNotPresentCreateNew(dto));
    }

    private Customer ifNotPresentCreateNew(CustomerDetailsRequestDTO dto) {
        logger.info("No result for requested user. Creating new one");
        AddressDTO addressDTO = dto.addressDTO();
        PersonDTO personDTO = dto.personDTO();

        Customer newCustomer = new Customer(personDTO.firstName(), personDTO.lastName(), personDTO.email(), personDTO.phoneNumber(),
                addressDTO.street(), addressDTO.city(), addressDTO.postalCode(), addressDTO.country());
        return customerRepository.save(newCustomer);


    }
}
