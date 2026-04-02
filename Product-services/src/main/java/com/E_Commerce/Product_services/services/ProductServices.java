package com.E_Commerce.Product_services.services;

import com.E_Commerce.Product_services.exception.ResourceNotFoundExceptions;
import com.E_Commerce.Product_services.model.dto.PageResponse;
import com.E_Commerce.Product_services.model.dto.ProductRequest;
import com.E_Commerce.Product_services.model.dto.ProductResponse;
import com.E_Commerce.Product_services.model.entity.Category;
import com.E_Commerce.Product_services.model.entity.Product;
import com.E_Commerce.Product_services.repository.CategoryRepository;
import com.E_Commerce.Product_services.repository.ProductRepository;
import com.E_Commerce.Product_services.util.ProductUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.List;

@Service
public class ProductServices {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private ProductUtil util = new ProductUtil();

    public ProductServices(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    //@Cacheable(value = "product", key = "'search=' + #search + ',categoryName=' + #categoryName + ',minPrice=' + #minPrice + ',maxPrice=' + #maxPrice + ',page=' + #page + ',size=' + #size")
    public PageResponse<ProductResponse> searchProductFilter(
            String search, String categoryName, Double minPrice, Double maxPrice, int page, int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.filterProduct(search, categoryName, minPrice, maxPrice, pageable);

        List<ProductResponse> products = productPage.getContent().stream().map(
                product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getStock()
                )
        ).toList();


        return new PageResponse<>(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    //getAllProduct, Obtener los productos, utilizando paginacion y cacheando los produtos
    @Cacheable(value = "products", key = "'page=' + #page + ',size=' + #size")
    public PageResponse<ProductResponse> getAllProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.findAll(pageable);

        List<ProductResponse> products = productsPage.getContent().stream().map(
                product -> new ProductResponse(
                        product.getCode(),
                        product.getName(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getStock()
                )
        ).toList();

        return new PageResponse<>(
                products,
                productsPage.getNumber(),
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages()
        );

    }

    //getByIDProduct, Obtener producto por Id.
    @Cacheable(value = "productsById", key = "#id")
    public ProductResponse getByIdProduct(Long id) throws ResourceNotFoundExceptions {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado")
        );

        return util.toProductResponse(product);
    }

    //Obtener producto por codigo
    @Cacheable(value = "productsBycode", key = "#code")
    public ProductResponse getProductByCode(String code) throws ResourceNotFoundExceptions {
        Product product = productRepository.findProductByCode(code).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado")
        );

        return util.toProductResponse(product);
    }

    //createNewProduct, Crear un nuevo producto
    @CachePut(value = "productById", key = "#result.code")
    @CacheEvict(value = {"productsAll", "productsByCategory", "categoryAll", "product"}, allEntries = true) // Crear nuevo producto -> actualizar cache
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
    @CacheEvict(value = {"productsAll", "productsByCategory", "categoryAll", "product"}, allEntries = true)// Actualizar producto → actualizar cache
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
    @CacheEvict(value = {"productsById", "productsAll", "productsByCategory", "categoryAll", "product"}, key = "#id", allEntries = true) // Eliminar producto → limpiar cache
    public void deleteProduct(Long id) throws ResourceNotFoundExceptions {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Producto no encotrado")
        );
        productRepository.delete(product);
    }
}
