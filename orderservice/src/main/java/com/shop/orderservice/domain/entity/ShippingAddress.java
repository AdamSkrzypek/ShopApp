package com.shop.orderservice.domain.entity;

import com.shop.orderservice.domain.dto.AddressDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShippingAddress {


    private  String street;
    private  String city;
    private  String postalCode;
    private  String country;

    public static ShippingAddress fromDto(AddressDTO dto) {
        return new ShippingAddress(dto.street(), dto.city(), dto.postalCode(), dto.country());
    }
}
