package com.shop.orderservice.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressDTO(@NotNull String street, @NotNull String city,
                         @NotNull @Size(min = 6, max = 6) String postalCode,
                         String country) {
}
