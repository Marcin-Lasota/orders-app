package com.example.ordersapp.orders.model.dto;

import com.example.ordersapp.orders.model.enums.OrderStatus;

public record OrderSearchParams(
    String customerId,
    OrderStatus status
) {

}
