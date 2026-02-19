package com.E_Commerce.Cart_services.repository;

import com.E_Commerce.Cart_services.model.data.StatusCart;
import com.E_Commerce.Cart_services.model.entity.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {


    Optional<ShoppingCart> findByUserCode(String userCode);
    Optional<ShoppingCart> findByCartCode(String cartCode);
    ShoppingCart findByUserCodeAndStatus(String userCode, StatusCart status);
    boolean existsByUserCode(String userCode);
}
