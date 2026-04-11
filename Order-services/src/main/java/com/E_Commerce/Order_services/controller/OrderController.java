package com.E_Commerce.Order_services.controller;

import com.E_Commerce.Order_services.model.dto.OrderResponce;
import com.E_Commerce.Order_services.services.OrderServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("e-commerce/api/v1/orders")
public class OrderController {

    private final OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }

    @PostMapping("/product/{productcode}/quantity/{productquantity}")
    public ResponseEntity<?> createNewOrderProduct(
            @RequestHeader("X-User-Code") String userCode,
            @PathVariable("productcode") String productCode,
            @PathVariable("productquantity") int productQuantity
    ){
        OrderResponce orderResponce = orderServices.createOrderFromSingleItem(userCode, productCode, productQuantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponce);
    }

    @PostMapping()
    public ResponseEntity<?> createNewOrderShoppingCart(
            @RequestHeader("X-User-Code") String userCode
    ){
        OrderResponce orderResponce = orderServices.createOrderFromCart(userCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponce);
    }
}
