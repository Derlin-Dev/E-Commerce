package com.E_Commerce.Cart_services.controller;

import com.E_Commerce.Cart_services.model.dto.AddItemRequest;
import com.E_Commerce.Cart_services.model.dto.NewQuantityItemRequest;
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

    @GetMapping("/getshoppingcart")
    public ResponseEntity<ShoppingCartResponse> getShoppingCart(@RequestHeader("X-User-Code") String userCode) throws Exception {
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

    @PutMapping("/upadatequantityitem/{codeCart}/item/{codeItem}")
    public ResponseEntity<?> updateQuantityItem(
            @PathVariable("codeCart") String codeCart,
            @PathVariable("codeItem") String codeItem,
            @RequestBody NewQuantityItemRequest newQuantityItemRequest
            )
    {

            shoppingCarServices.updateQuantityItem(codeCart, codeItem, newQuantityItemRequest.getNewQuantity());
            return ResponseEntity.ok("Producto actualizado correctamente");
    }

    @DeleteMapping("/delete/{codeCart}/product/{codeProduct}")
    public  ResponseEntity<?> removeItemCart(@RequestHeader("X-User-Code") String userCode,
                                             @PathVariable String codeCart,
                                             @PathVariable String codeProduct){

        return ResponseEntity.ok( shoppingCarServices.removeItemCart(codeCart, userCode, codeProduct));

    }

}
