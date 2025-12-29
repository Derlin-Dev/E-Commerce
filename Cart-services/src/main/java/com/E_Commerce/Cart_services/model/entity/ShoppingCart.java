package com.E_Commerce.Cart_services.model.entity;

import com.E_Commerce.Cart_services.model.data.StatusCart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Shopping_Cart")
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userCode;
    private String cartCode;
    private StatusCart status;

    @Column(nullable = false, updatable = false)
    private LocalDate createAt;

    @Column(nullable = false)
    private LocalDate updateAt;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCart> productCartList;

    public ShoppingCart(String userCode, String cartCode, StatusCart status, LocalDate createAt) {
        this.userCode = userCode;
        this.cartCode = cartCode;
        this.status = status;
        this.createAt = createAt;
    }
}
