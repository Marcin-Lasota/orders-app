package com.example.ordersapp.orders.service;

import com.example.ordersapp.orders.model.dto.NewOrderDto;
import com.example.ordersapp.orders.model.dto.OrderDetailsDto;
import com.example.ordersapp.orders.model.dto.OrderInfoDto;
import com.example.ordersapp.orders.model.dto.OrderSearchParams;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    NewOrderDto createOrder(NewOrderDto order);

    Optional<OrderDetailsDto> getOrderDetailsById(Long id);

    Page<OrderInfoDto> getOrders(Pageable pageable, OrderSearchParams searchParams);

    void deleteOrder(Long id);

    Optional<OrderDetailsDto> updateOrder(Long id, OrderDetailsDto updatedOrder);

    Optional<OrderDetailsDto> updateOrderPartially(Long id, OrderDetailsDto updatedOrder);
}
