package com.E_Commerce.Cart_services.controller;

import com.E_Commerce.Cart_services.model.dto.AddItemRequest;
import com.E_Commerce.Cart_services.model.dto.ShoppingCartResponse;
import com.E_Commerce.Cart_services.services.ShoppingCarServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/e-commerce/api/v1/shoppingcart")
public class ShoppingCartController {

    private final ShoppingCarServices shoppingCarServices;

    public ShoppingCartController(ShoppingCarServices shoppingCarServices) {
        this.shoppingCarServices = shoppingCarServices;
    }

    @GetMapping("/getshoppingcart/{usercode}")
    public ResponseEntity<ShoppingCartResponse> getShoppingCart(@PathVariable("usercode") String userCode) throws Exception {
        ShoppingCartResponse shoppingCartResponse = shoppingCarServices.getShoppingCartByUser(userCode);
        return ResponseEntity.ok(shoppingCartResponse);
    }

    @PostMapping("/createshoppingcart")
    public ResponseEntity<ShoppingCartResponse> createShoppingCart(@RequestHeader("X-User-Code") String userCode) throws Exception {
        ShoppingCartResponse shoppingCartResponse = shoppingCarServices.createNewShoppingCart(userCode);
        return ResponseEntity.status(HttpStatus.CREATED).body(shoppingCartResponse);
    }

    @PostMapping("/additem")
    public ResponseEntity<?> addNewItemshoppingCart(@RequestBody AddItemRequest request){
            return ResponseEntity.ok(shoppingCarServices.addItem(
                    request.getCartCode(), request.getProductCode(), request.getQuantity()
            ));
    }

    @DeleteMapping("/delete/car/{codeCart}/product/{codeProduct}")
    public  ResponseEntity<?> removeItemCart(@RequestHeader("X-User-Code") String userCode,
                                             @PathVariable String codeCart,
                                             @PathVariable String codeProduct){


        return ResponseEntity.ok( shoppingCarServices.removeItemCart(codeCart, userCode, codeProduct));

    }

}
