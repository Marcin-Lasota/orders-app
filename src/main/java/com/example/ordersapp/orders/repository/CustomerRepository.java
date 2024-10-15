package com.example.ordersapp.orders.repository;

import com.example.ordersapp.orders.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
