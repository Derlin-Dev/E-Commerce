package com.E_Commerce.Product_services.controller;

import com.E_Commerce.Product_services.exception.ResourceNotFoundExceptions;
import com.E_Commerce.Product_services.model.dto.ProductRequest;
import com.E_Commerce.Product_services.model.dto.ProductResponse;
import com.E_Commerce.Product_services.model.entity.Product;
import com.E_Commerce.Product_services.services.ProductServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/e-commerce/api/v1/product")
public class ProductController {

    private final ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    //obtener todos los productos
    @GetMapping("/get")
    public ResponseEntity<?> getProducts(){
        List<ProductResponse> productResponses = productServices.getAllProduct();
        return ResponseEntity.status(HttpStatus.OK).body(productResponses);
    }

    //Obtener productos por su id
    @GetMapping("/get/{id}")
    public ResponseEntity<ProductResponse>getByIdProduct(@PathVariable Long id) throws ResourceNotFoundExceptions {
        ProductResponse response = productServices.getByIdProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Obtener producto por codigo
    @GetMapping("/getcode/{code}")
    public ResponseEntity<ProductResponse> getByProductCode(@PathVariable String code) throws  ResourceNotFoundExceptions {
        ProductResponse response = productServices.getProductByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //Crear un nuevo producto

    @PostMapping("/create")
    public ResponseEntity<?> registerNewProduct(@RequestBody ProductRequest request) throws ResourceNotFoundExceptions {
        Product product = productServices.createNewProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    //Editar producto
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) throws ResourceNotFoundExceptions {
        productServices.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.OK).body("producto actualizado!");
    }

    //Eliminar producto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) throws ResourceNotFoundExceptions {
        productServices.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Producto eliminado");
    }
}
