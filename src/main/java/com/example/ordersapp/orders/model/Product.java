package com.example.ordersapp.orders.model;

import com.example.ordersapp.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {

    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    private Integer stockQuantity;

    public void subtractFromStock(int quantity) {
        stockQuantity = stockQuantity - quantity;
    }
}
