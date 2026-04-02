package com.E_Commerce.Product_services.model.dto;
import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryProductsPageResponse {
    private String code;
    private String nameCategory;
    private PageResponse<ProductResponse> products;

}
