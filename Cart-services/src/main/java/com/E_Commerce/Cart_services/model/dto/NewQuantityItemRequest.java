package com.E_Commerce.Cart_services.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewQuantityItemRequest {
    private int newQuantity;
}
