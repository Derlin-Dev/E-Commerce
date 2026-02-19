package com.E_Commerce.Cart_services.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cart_item")
public class ProductCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productCode;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double subTotal;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private ShoppingCart shoppingCart;

    public ProductCart(
            String product_code, String product_name, double unit_price, int quantity,
            double sub_total, ShoppingCart shoppingCart
    ) {
        this.productCode = product_code;
        this.productName = product_name;
        this.unitPrice = unit_price;
        this.quantity = quantity;
        this.subTotal = sub_total;
        this.shoppingCart = shoppingCart;
    }
}
