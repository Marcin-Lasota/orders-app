package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.Product;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Length;

/**
 * DTO for {@link Product}
 */
public record ProductDto(
    Long id,
    @Length(min = 3, max = 50) String name,
    @Length(max = 255) String description,
    @Digits(integer = 10, fraction = 2) BigDecimal price,
    @PositiveOrZero Integer stockQuantity
) implements Serializable {

}
