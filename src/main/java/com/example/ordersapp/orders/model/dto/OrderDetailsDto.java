package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;

/**
 * DTO for {@link Order}
 */
@Value
public class OrderDetailsDto {

    Long id;
    CustomerDto customer;
    List<OrderItemDto> orderItems;
    Integer totalItems;
    BigDecimal totalPrice;
    OrderStatus status;
    PaymentMethod paymentMethod;
    LocalDateTime orderDate;

    public OrderDetailsDto(Long id, CustomerDto customer, List<OrderItemDto> orderItems,
        OrderStatus status, PaymentMethod paymentMethod, LocalDateTime orderDate) {
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;

        if (orderItems == null) {
            this.totalItems = null;
            this.totalPrice = null;
        } else {
            this.totalItems = orderItems.stream()
                .mapToInt(OrderItemDto::quantity)
                .sum();

            this.totalPrice = orderItems.stream()
                .map(orderItem -> {
                    BigDecimal quantity = BigDecimal.valueOf(orderItem.quantity());
                    return orderItem.unitPrice().multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
