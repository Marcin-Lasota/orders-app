package com.example.ordersapp.orders.model;

import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import com.example.ordersapp.shared.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<OrderItem> orderItems;

    @Transient
    private BigDecimal totalPrice;
    @Transient
    private int totalItems;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime orderDate;
}
