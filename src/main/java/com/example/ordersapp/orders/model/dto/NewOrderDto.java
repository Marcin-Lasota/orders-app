package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Order}
 */
public record NewOrderDto(
    Long id,
    @NotNull Long customerId,
    @Size(min = 1) List<OrderItemDto> orderItems,
    OrderStatus status,
    @NotNull PaymentMethod paymentMethod,
    LocalDateTime orderDate)
    implements Serializable {

}
