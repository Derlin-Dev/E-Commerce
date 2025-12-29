package com.E_Commerce.Cart_services.model.dto;

import com.E_Commerce.Cart_services.model.data.StatusCart;
import com.E_Commerce.Cart_services.model.entity.ProductCart;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShoppingCartResponse {

    private String userCode;
    private String cartCode;
    private StatusCart status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<ProductCart> productCartList;

    public ShoppingCartResponse(String userCode, String cartCode, StatusCart status, LocalDate createAt, LocalDate updateAt, List<ProductCart> productCartList) {
    }
}
