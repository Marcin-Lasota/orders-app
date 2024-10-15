package com.example.ordersapp.orders.service;

import com.example.ordersapp.orders.model.dto.CustomerDto;
import java.util.Optional;

public interface CustomerService {

    CustomerDto createCustomer(CustomerDto order);

    Optional<CustomerDto> getCustomerById(Long id);

    void deleteCustomer(Long id);

    Optional<CustomerDto> updateCustomer(Long id, CustomerDto updatedCustomer);

    Optional<CustomerDto> updateCustomerPartially(Long id, CustomerDto updatedOrder);
}
