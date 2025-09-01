package com.E_Commerce.Product_services.services;

import com.E_Commerce.Product_services.exception.ResourceNotFoundExceptions;
import com.E_Commerce.Product_services.model.dto.CategoryRequest;
import com.E_Commerce.Product_services.model.dto.CategoryResponse;
import com.E_Commerce.Product_services.model.dto.ProductResponse;
import com.E_Commerce.Product_services.model.entity.Category;
import com.E_Commerce.Product_services.model.entity.Product;
import com.E_Commerce.Product_services.repository.CategoryRepository;
import com.E_Commerce.Product_services.repository.ProductRepository;
import com.E_Commerce.Product_services.util.ProductUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServices {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private ProductUtil util = new ProductUtil();

    public CategoryServices(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Cacheable(value = "categoryAll")
    public List<CategoryResponse> getAllCategory() throws ResourceNotFoundExceptions{
        List<Category> categories = categoryRepository.findAll();

        if (categories == null) throw new ResourceNotFoundExceptions("Categorias no encontradas");

        return categories.stream().map(
                category -> new CategoryResponse(
                        category.getCode(),
                        category.getNameCategory(),
                        category.getProducts()
                )
        ).collect(Collectors.toList());
    }

    //getByCategoryProduct, Obtener productos por categoria
    @Cacheable(value = "productsByCategory", key = "#id_category")
    public List<ProductResponse> getByCategoryProduct(Long id_category) throws ResourceNotFoundExceptions {
        List<Product> products = productRepository.findByCategoryId(id_category);

        if (!categoryRepository.existsById(id_category)){
            throw new ResourceNotFoundExceptions("La categoria no existe");
        }
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getDescription(),
                        product.getStock()
                )).collect(Collectors.toList());
    }

    //createNewCategory, Crear nueva categoria
    @CacheEvict(value = "categoryAll", allEntries = true)
    public void createNewCategory(CategoryRequest request){
        Optional<Category> opCategory = categoryRepository.findByNameCategory(request.getNameCategory());

        if (opCategory.isPresent()){
            throw new RuntimeException("La categoria ya esta registrada");
        }
        String code = util.generateCodeProduct("CATG-");
        Category category = new Category(
                code,
                request.getNameCategory()
        );

        categoryRepository.save(category);
    }
}
