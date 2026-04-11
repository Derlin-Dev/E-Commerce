package com.E_Commerce.Order_services.services;

import com.E_Commerce.Order_services.model.data.OrderStatus;
import com.E_Commerce.Order_services.model.dto.*;
import com.E_Commerce.Order_services.model.entity.Order;
import com.E_Commerce.Order_services.model.entity.OrderDetails;
import com.E_Commerce.Order_services.repository.OrderRepository;
import com.E_Commerce.Order_services.util.OrderUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServices {

    private final OrderRepository orderRepository;
    private final OrderUtil orderUtil;
    private final RestTemplate restTemplate;

    private static String PRODUCT_SERVICE_GET = "http://Product-services/e-commerce/api/v1/product/code/{code}";
    private static String SHOPPINGCART_SERVICE_GET = "http://Cart-services/e-commerce/api/v1/shoppingcart/getshoppingcart";

    public OrderServices(OrderRepository orderRepository, OrderUtil orderUtil, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderUtil = orderUtil;
        this.restTemplate = restTemplate;
    }

    //Creamos una orden de un solo item sin pasar por el carrito de compreaas
    public OrderResponce createOrderFromSingleItem(String userCode, String itemCode, int quantity){

        ProductServicesRequest productServicesRequest = getItemProductService(itemCode);

        if (productServicesRequest == null) throw new RuntimeException("Producto no encotrado");

        double subTotal = productServicesRequest.getPrice() * quantity;

       ProductRequest productRequest = new ProductRequest(
                productServicesRequest.getCode(),
                productServicesRequest.getName(),
                productServicesRequest.getPrice(),
                quantity,
                subTotal);

        return createNewOrder(userCode, List.of(productRequest));
    }

    //Obtenemos el producto haciendo una llamada HTTP a Product-services
    public ProductServicesRequest getItemProductService(String code){
        String url = PRODUCT_SERVICE_GET;
        return restTemplate.getForObject(url, ProductServicesRequest.class, code);
    }

    //Creamos una orden desde el carrito de compra
    public OrderResponce createOrderFromCart(String userCode){
        ShoppingCartRequest shoppingCartRequest = getAllItemShoppingCart(userCode);

        return createNewOrder(userCode, shoppingCartRequest.getProductCartList());
    }

    //Obtenermos el carrito de compras haciendo una llamada HTTP a Cart-Servises
    public ShoppingCartRequest getAllItemShoppingCart(String userCode){

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Code", userCode);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

        ResponseEntity<ShoppingCartRequest> response = restTemplate.exchange(
                SHOPPINGCART_SERVICE_GET,
                HttpMethod.GET,
                entity,
                ShoppingCartRequest.class
        );

        return response.getBody();
    }


    //Creamos una orden desde un solo item/producto
    public OrderResponce createNewOrder(String userCode, List<ProductRequest> items){
        String orderCode = orderUtil.generateOrderCode("ORDER-");

        Order newOrder = new Order(
                orderCode,
                userCode,
                0,
                OrderStatus.PENDING_PAYMENT,
                LocalDateTime.now()
        );

        double total = 0;

        for(ProductRequest item : items){

            total += item.getSubTotal();

            OrderDetails orderDetails = new OrderDetails(
                    item.getCode(),
                    item.getName(),
                    item.getPrice(),
                    item.getQuantity(),
                    item.getSubTotal()
            );

            newOrder.addOrderDetail(orderDetails);
        }

        newOrder.setTotal(total);

        orderRepository.save(newOrder);
        return new OrderResponce(
                orderCode,
                userCode,
                total
        );
    }

}
