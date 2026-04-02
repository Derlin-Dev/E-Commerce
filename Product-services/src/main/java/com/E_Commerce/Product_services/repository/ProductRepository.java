package com.E_Commerce.Product_services.repository;

import com.E_Commerce.Product_services.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    Optional<Product> findProductByCode(String code);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query(value = """
        SELECT p.*
        FROM product p
        JOIN category c ON c.id = p.id_category
        WHERE (:search IS NULL OR
               unaccent(p.name) ILIKE unaccent(CONCAT('%', :search, '%')) OR
               unaccent(p.description) ILIKE unaccent(CONCAT('%', :search, '%')))
        AND (:nameCategory IS NULL OR unaccent(c.name_category) ILIKE unaccent(CONCAT('%', :nameCategory, '%')))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM product p
        JOIN category c ON c.id = p.id_category
        WHERE (:search IS NULL OR
               unaccent(p.name) ILIKE unaccent(CONCAT('%', :search, '%')) OR
               unaccent(p.description) ILIKE unaccent(CONCAT('%', :search, '%')))
        AND (:nameCategory IS NULL OR unaccent(c.name_category) ILIKE unaccent(CONCAT('%', :nameCategory, '%')))
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        """,
            nativeQuery = true)
    Page<Product> filterProduct(
            @Param("search") String search,
            @Param("nameCategory") String nameCategory,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

}
