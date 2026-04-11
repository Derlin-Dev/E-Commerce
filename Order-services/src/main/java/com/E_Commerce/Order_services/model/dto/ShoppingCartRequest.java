package com.E_Commerce.Order_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartRequest {
    private String userCode;
    private String cartCode;
    private LocalDate createAt;
    private LocalDate updateAt;
    private List<ProductRequest> productCartList;
}
