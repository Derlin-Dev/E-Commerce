package com.E_Commerce.Order_services.model.entity;

import com.E_Commerce.Order_services.model.data.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderCode;
    private String userCode;
    private double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetails> oderDetails = new ArrayList<>();

    public Order(String orderCode, String userCode, double total, OrderStatus status, LocalDateTime createAt) {
        this.orderCode = orderCode;
        this.userCode = userCode;
        this.total = total;
        this.status = status;
        this.createAt = createAt;
    }

    public void addOrderDetail(OrderDetails details){
        oderDetails.add(details);
        details.setOrder(this);
    }
}
