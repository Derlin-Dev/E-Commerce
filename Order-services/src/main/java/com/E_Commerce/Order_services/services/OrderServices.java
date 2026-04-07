package com.E_Commerce.Order_services.services;

import com.E_Commerce.Order_services.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServices {

    private final OrderRepository orderRepository;

    public OrderServices(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


}
