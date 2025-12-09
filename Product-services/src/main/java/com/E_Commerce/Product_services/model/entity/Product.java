package com.E_Commerce.Product_services.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private double price;
    private String description;
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_id_category",
                    foreignKeyDefinition = "FOREIGN KEY (id_category) REFERENCES Category(id) ON DELETE CASCADE"))
    @JsonIgnore
    Category category;

    public Product(String code, String name, double price, String description, int stock, Category category) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.category = category;
    }
}
