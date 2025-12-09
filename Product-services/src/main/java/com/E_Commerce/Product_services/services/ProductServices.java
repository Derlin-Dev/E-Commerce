package com.E_Commerce.Product_services.services;

import com.E_Commerce.Product_services.exception.ResourceNotFoundExceptions;
import com.E_Commerce.Product_services.model.dto.CategoryRequest;
import com.E_Commerce.Product_services.model.dto.ProductResponse;
import com.E_Commerce.Product_services.model.dto.ProductRequest;
import com.E_Commerce.Product_services.model.entity.Category;
import com.E_Commerce.Product_services.model.entity.Product;
import com.E_Commerce.Product_services.repository.CategoryRepository;
import com.E_Commerce.Product_services.repository.ProductRepository;
import com.E_Commerce.Product_services.util.ProductUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServices {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private ProductUtil util = new ProductUtil();

    public ProductServices(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    //getAllProduct, Obtener los productos
    @Cacheable(value = "productsAll")
    public List<ProductResponse> getAllProduct(){
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getStock()
                )).collect(Collectors.toList());
    }

    //getByIDProduct, Obtener producto por Id.
    @Cacheable(value = "productsById", key = "#id")
    public ProductResponse getByIdProduct(Long id) throws ResourceNotFoundExceptions {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado")
        );

        return util.toProductResponse(product);
    }

    //createNewProduct, Crear un nuevo producto
    @CachePut(value = "productById", key = "#result.code")
    @CacheEvict(value = {"productsAll", "productsByCategory", "categoryAll"}, allEntries = true) // Crear nuevo producto -> actualizar cache
    public Product createNewProduct(ProductRequest request) throws ResourceNotFoundExceptions {
        Category category = categoryRepository.findById(request.getId_Category()).orElseThrow(
                () -> new ResourceNotFoundExceptions("Categoria no encontrada")
        );

        String code = util.generateCodeProduct("PROD-");
        Product product = new Product(
                code,
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getStock(),
                category
        );
       return productRepository.save(product);
    }

    //updateProduct, Acutalizar productos
    @CachePut(value = "productById", key = "#id")
    @CacheEvict(value = {"productsAll", "productsByCategory", "categoryAll"}, allEntries = true)// Actualizar producto → actualizar cache
    public void updateProduct(Long id, ProductRequest request) throws ResourceNotFoundExceptions {

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado no encontrada")
        );

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setStock(product.getStock());

        productRepository.save(product);
    }

    //deleteProducto, eliminar productos
    @CacheEvict(value = {"productsById", "productsAll", "productsByCategory", "categoryAll"}, key = "#id", allEntries = true) // Eliminar producto → limpiar cache
    public void deleteProduct(Long id) throws ResourceNotFoundExceptions {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado")
        );
        productRepository.delete(product);
    }

}
