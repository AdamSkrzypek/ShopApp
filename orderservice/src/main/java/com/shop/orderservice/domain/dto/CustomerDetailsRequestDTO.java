package com.shop.orderservice.domain.dto;

import jakarta.validation.constraints.NotNull;

public record CustomerDetailsRequestDTO(@NotNull PersonDTO personDTO, @NotNull AddressDTO addressDTO) {
}
