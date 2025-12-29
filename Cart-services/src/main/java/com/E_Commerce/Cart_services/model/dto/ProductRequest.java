package com.E_Commerce.Cart_services.model.dto;

import lombok.Data;

@Data
public class ProductRequest {
    private String code;
    private String name;
    private double price;
    private String description;
}
