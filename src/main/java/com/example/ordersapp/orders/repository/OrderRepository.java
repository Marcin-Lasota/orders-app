package com.example.ordersapp.orders.repository;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.dto.OrderInfoDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT new com.example.ordersapp.orders.model.dto.OrderInfoDto
        (o.id, c.id, o.status, o.paymentMethod, o.orderDate)
        FROM Order o JOIN o.customer c
        """)
    Page<OrderInfoDto> findAllWithCustomerId(Example<Order> orderExample, Pageable pageable);

}
