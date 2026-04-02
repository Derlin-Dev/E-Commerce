package com.E_Commerce.Product_services.services;

import com.E_Commerce.Product_services.exception.ResourceNotFoundExceptions;
import com.E_Commerce.Product_services.model.dto.*;
import com.E_Commerce.Product_services.model.entity.Category;
import com.E_Commerce.Product_services.model.entity.Product;
import com.E_Commerce.Product_services.repository.CategoryRepository;
import com.E_Commerce.Product_services.repository.ProductRepository;
import com.E_Commerce.Product_services.util.ProductUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

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

        if (categories.isEmpty()) throw new ResourceNotFoundExceptions("Categorias no encontradas");

        return categories.stream().map(
                category -> new CategoryResponse(
                        category.getCode(),
                        category.getNameCategory()
                )
        ).collect(Collectors.toList());
    }

    //getByCategoryProduct, Obtener productos por categoria
    @Cacheable(value = "productsByCategory", key = "'cat=' + #id_category + ',page=' + #page + ',size=' + #size")
    public CategoryProductsPageResponse getCategoryWithProductsPaged(Long id_category , int page, int size) throws ResourceNotFoundExceptions {

        Category category = categoryRepository.findById(id_category).orElseThrow(
                () -> new ResourceNotFoundExceptions("La categoria no existe")
        );
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findByCategoryId(id_category, pageable);

        List<ProductResponse> products = productPage.getContent().stream()
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getStock()
                ))
                .toList();

        PageResponse<ProductResponse> pageResponse = new PageResponse<>(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );

        return new CategoryProductsPageResponse(
                category.getCode(),
                category.getNameCategory(),
                pageResponse
        );
    }

    //createNewCategory, Crear nueva categoria
    @CacheEvict(value = {"categoryAll", "productsByCategory"}, allEntries = true)
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
