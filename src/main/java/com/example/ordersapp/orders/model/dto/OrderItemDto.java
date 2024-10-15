package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.OrderItem;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link OrderItem}
 */
public record OrderItemDto(
    Long id,
    ProductDto product,
    @Positive int quantity,
    BigDecimal unitPrice
) implements Serializable {

}
