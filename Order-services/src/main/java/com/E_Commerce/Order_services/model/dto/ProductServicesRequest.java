package com.E_Commerce.Order_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductServicesRequest {
    private String code;
    private String name;
    private double price;
}
