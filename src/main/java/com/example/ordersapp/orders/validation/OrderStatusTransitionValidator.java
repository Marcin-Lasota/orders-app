package com.example.ordersapp.orders.validation;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import jakarta.validation.ValidationException;

public class OrderStatusTransitionValidator {

    private OrderStatusTransitionValidator() {
    }

    public static void validate(Order order, OrderStatus newStatus) {
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case CREATED -> newStatus == OrderStatus.ACCEPTED || newStatus == OrderStatus.CANCELLED;
            case ACCEPTED -> newStatus == OrderStatus.SENT;
            case SENT -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED -> false; // Cannot transition from DELIVERED
            case CANCELLED -> false; // Cannot transition from CANCELLED
        };

        if (!valid) {
            throw new ValidationException("Invalid order status change %s -> %s"
                .formatted(currentStatus, newStatus));
        }
    }
}
