package com.E_Commerce.Product_services.model.dto;

import com.E_Commerce.Product_services.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String code;
    private String nameCategory;
    private List<Product> products;
}
