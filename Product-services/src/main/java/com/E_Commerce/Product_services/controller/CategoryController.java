package com.E_Commerce.Product_services.controller;

import com.E_Commerce.Product_services.model.dto.CategoryRequest;
import com.E_Commerce.Product_services.services.CategoryServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/e-commerce/api/v1/category")
public class CategoryController {

    private final CategoryServices categoryServices;

    public CategoryController(CategoryServices categoryServices) {
        this.categoryServices = categoryServices;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllCategory(){
        return null;
    }

    @GetMapping("/getproductbycategory")
    public ResponseEntity<?> getProductByCategory(){
        return null;
    }

    @PostMapping("/newCategory")
    public ResponseEntity<?> crearteCategory(@RequestBody CategoryRequest request){
        try {
            categoryServices.createNewCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Categoria creada");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria no creada");
        }
    }
}
