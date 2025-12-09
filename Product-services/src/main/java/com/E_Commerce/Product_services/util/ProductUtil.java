package com.E_Commerce.Product_services.util;

import com.E_Commerce.Product_services.model.dto.ProductResponse;
import com.E_Commerce.Product_services.model.dto.ProductRequest;
import com.E_Commerce.Product_services.model.entity.Category;
import com.E_Commerce.Product_services.model.entity.Product;

import java.security.SecureRandom;

public class ProductUtil {

    //Variables para el metodo de genedar codigos
    private static final String CHARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom ramdon = new SecureRandom();

    public ProductResponse toProductResponse(Product product){
        return new ProductResponse(
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock()
        );
    }

    public Product toProductEntity(String code, ProductRequest request, Category category){
        return new Product(
                code,
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getStock(),
                category
        );
    }

    public String generateCodeProduct(String n){

        StringBuilder code = new StringBuilder(n);
        for (int i = 0; i < CODE_LENGTH; i++){
            int index = ramdon.nextInt(CHARACTERES.length());
            code.append(CHARACTERES.charAt(index));
        }

        return code.toString();
    }

}
