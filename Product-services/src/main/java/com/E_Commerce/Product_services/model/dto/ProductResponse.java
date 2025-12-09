package com.E_Commerce.Product_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String code;
    private String name;
    private double price;
    private String descripcion;
    private int stock;
}
