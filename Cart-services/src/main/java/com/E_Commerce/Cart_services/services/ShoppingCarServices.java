package com.E_Commerce.Cart_services.services;

import com.E_Commerce.Cart_services.model.data.StatusCart;
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
import java.util.Optional;

@Service
public class ShoppingCarServices {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductCartRepository productCartRepository;
    private final RestTemplate restTemplate;
    private final ShoppingCartUtil shoppingCartUtil;

    private static String PRODUCT_SERVICE_GET = "http://Product-services/e-commerce/api/v1/product/getcode/{code}";

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

        ShoppingCartResponse response = new ShoppingCartResponse(
                cart.getUserCode(),
                cart.getCartCode(),
                cart.getStatus(),
                cart.getCreateAt(),
                cart.getUpdateAt(),
                cart.getProductCartList()
        );
        
        return response;
    }

    //Crear nuevo carrito de compras
    public ShoppingCartResponse createNewShoppingCart(String userCode) throws Exception {

        if (shoppingCartRepository.existsByUserCode(userCode)) {
            throw new Exception("Carrito ya registrado");
        }

        String cartCode = shoppingCartUtil.generateShoppingCartCode("CART-");

        ShoppingCart shoppingCart =
                new ShoppingCart(userCode, cartCode, StatusCart.ACTIVE, LocalDate.now(), LocalDate.now());
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

}
