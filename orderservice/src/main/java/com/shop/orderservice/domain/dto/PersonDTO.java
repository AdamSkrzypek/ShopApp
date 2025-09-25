package com.shop.orderservice.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PersonDTO(@NotNull @Size(min = 3) String firstName, @NotNull @Size(min = 3) String lastName,
                        @Email String email,  @Pattern(regexp = "^\\+48\\s?\\d{9}$") String phoneNumber) {
}
