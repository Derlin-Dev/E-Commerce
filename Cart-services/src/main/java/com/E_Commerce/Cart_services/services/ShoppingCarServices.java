package com.E_Commerce.Cart_services.services;

import com.E_Commerce.Cart_services.model.data.StatusCart;
import com.E_Commerce.Cart_services.model.dto.ProductCartResponse;
import com.E_Commerce.Cart_services.model.dto.ProductRequest;
import com.E_Commerce.Cart_services.model.dto.ShoppingCartResponse;
import com.E_Commerce.Cart_services.model.entity.ProductCart;
import com.E_Commerce.Cart_services.model.entity.ShoppingCart;
import com.E_Commerce.Cart_services.repository.ProductCartRepository;
import com.E_Commerce.Cart_services.repository.ShoppingCartRepository;
import com.E_Commerce.Cart_services.util.ShoppingCartUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCarServices {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductCartRepository productCartRepository;
    private final RestTemplate restTemplate;
    private final ShoppingCartUtil shoppingCartUtil;

    private static String PRODUCT_SERVICE_GET = "http://Product-services/e-commerce/api/v1/product/code/{code}";

    //Constructor
    public ShoppingCarServices(ShoppingCartRepository shoppingCartRepository, ProductCartRepository productCartRepository,
                               RestTemplate restTemplate, ShoppingCartUtil shoppingCartUtil)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productCartRepository = productCartRepository;
        this.restTemplate = restTemplate;
        this.shoppingCartUtil = shoppingCartUtil;
    }

    //Obtener carrito de compras de un usuario
    public ShoppingCartResponse getShoppingCartByUser(String userCode) throws Exception {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByUserCode(userCode);

        if (shoppingCart.isEmpty()){
            return createNewShoppingCart(userCode);
        }

        return shoppingCartConvert(shoppingCart.get());
    }

    public ShoppingCartResponse shoppingCartConvert(ShoppingCart cart){
        ShoppingCartResponse response = new ShoppingCartResponse();
        response.setUserCode(cart.getUserCode());
        response.setCartCode(cart.getCartCode());
        response.setStatus(cart.getStatus());
        response.setCreateAt(cart.getCreateAt());
        response.setUpdateAt(cart.getUpdateAt());

        List<ProductCartResponse> products = cart.getProductCartList()
                .stream()
                .map(p -> {
                    ProductCartResponse dto = new ProductCartResponse();
                    dto.setCode(p.getProductCode());
                    dto.setName(p.getProductName());
                    dto.setPrice(p.getUnitPrice());
                    dto.setQuantity(p.getQuantity());
                    dto.setSubTotal(p.getSubTotal());
                    return dto;
                })
                .toList();

        response.setProductCartList(products);

        return response;
    }

    //Crear nuevo carrito de compras
    public ShoppingCartResponse createNewShoppingCart(String userCode) throws Exception {

        if (shoppingCartRepository.existsByUserCode(userCode)) {
            throw new Exception("Carrito ya registrado");
        }

        String cartCode = shoppingCartUtil.generateShoppingCartCode("CART-");

        ShoppingCart shoppingCart = new ShoppingCart(
                userCode, cartCode, StatusCart.ACTIVE, LocalDate.now(), LocalDate.now()
        );

        return shoppingCartConvert(shoppingCartRepository.save(shoppingCart));
    }


    //Obtener el item del servicio de productos
    public ProductRequest getProductByCode(String code){
        String url = PRODUCT_SERVICE_GET ;
        return restTemplate.getForObject(url, ProductRequest.class, code);
    }

    //Agregar producto al carrito
    public String addItem(String cartCode, String productCode, int quantity){

        ShoppingCart shoppingCart = shoppingCartRepository.findByCartCode(cartCode)
                .orElseThrow(() -> new RuntimeException("Carrito no encotrado"));

        ProductRequest productRequest = getProductByCode(productCode);

        if (productRequest == null){
            throw new RuntimeException(("Producto no encotrado"));
        }

        double subTotal = productRequest.getPrice() * quantity;

        try {
            ProductCart productCart = new ProductCart(
                    productRequest.getCode(),
                    productRequest.getName(),
                    productRequest.getPrice(),
                    quantity,
                    subTotal,
                    shoppingCart
            );
            productCartRepository.save(productCart);
            return "Producto agregado correctamente";
        }catch (RuntimeException r) {
            throw new RuntimeException("Error al procesar la solicitud.!");
        }

    }

    //Remover producto del carrito
    public ShoppingCart removeItemCart(String codeCar, String cadeUser, String codeProduct){

        ShoppingCart shoppingCart = shoppingCartRepository.findByCartCode(codeCar)
                .orElseThrow(() -> new RuntimeException("Carrito no encotrado"));

        if(!shoppingCart.getUserCode().equals(cadeUser)) throw new RuntimeException("El Carrito no pertenese al usuario");

        boolean remove = shoppingCart.getProductCartList()
                .removeIf(p -> p.getProductCode().equals(codeProduct));

        if (!remove) throw new RuntimeException("Producto no encontrado");

        return shoppingCartRepository.save(shoppingCart);

    }

    //Actualizar producto del carrito
    public void updatePricesItem(){

    }

    //Actualizar cantidad del producto del carrito
    public void updateQuantityItem(String codeCar, String codeProduct, int newQuantity) throws RuntimeException{

        ShoppingCart shoppingCart = shoppingCartRepository.findByCartCode(codeCar)
                .orElseThrow( () -> new RuntimeException("Carrito no encotrado"));

        ProductCart productCart = shoppingCart.getProductCartList()
                .stream()
                .filter(p -> p.getProductCode().equals(codeProduct))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Proudcto no encotrado en el carrito"));

        productCart.setQuantity(newQuantity);

        shoppingCartRepository.save(shoppingCart);
    }
}
