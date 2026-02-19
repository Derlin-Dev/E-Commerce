package com.E_Commerce.Cart_services.model.dto;

import com.E_Commerce.Cart_services.model.data.StatusCart;
import com.E_Commerce.Cart_services.model.entity.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCartResponse {

    private String userCode;
    private String cartCode;
    private StatusCart status;
    private LocalDate createAt;
    private LocalDate updateAt;
    private List<ProductCart> productCartList;


}
