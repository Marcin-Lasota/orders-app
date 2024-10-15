package com.example.ordersapp.orders.model;

import com.example.ordersapp.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private int quantity;

    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

}
