package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Order}
 */
public record OrderInfoDto(
    Long id,
    @NotNull Long customerId,
    @NotNull OrderStatus status,
    @NotNull PaymentMethod paymentMethod,
    LocalDateTime orderDate)
    implements Serializable {

}
