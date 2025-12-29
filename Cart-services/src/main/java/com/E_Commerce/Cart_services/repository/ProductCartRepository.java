package com.E_Commerce.Cart_services.repository;

import com.E_Commerce.Cart_services.model.entity.ProductCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCartRepository extends JpaRepository<ProductCart, Long> {

}
