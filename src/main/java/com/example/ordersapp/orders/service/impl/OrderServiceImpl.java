package com.example.ordersapp.orders.service.impl;

import static java.util.stream.Collectors.toMap;

import com.example.ordersapp.orders.model.Customer;
import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.OrderItem;
import com.example.ordersapp.orders.model.Product;
import com.example.ordersapp.orders.model.dto.NewOrderDto;
import com.example.ordersapp.orders.model.dto.OrderDetailsDto;
import com.example.ordersapp.orders.model.dto.OrderInfoDto;
import com.example.ordersapp.orders.model.dto.OrderSearchParams;
import com.example.ordersapp.orders.model.dto.mapper.OrderMapper;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import com.example.ordersapp.orders.repository.CustomerRepository;
import com.example.ordersapp.orders.repository.OrderRepository;
import com.example.ordersapp.orders.repository.ProductRepository;
import com.example.ordersapp.orders.service.OrderService;
import com.example.ordersapp.orders.validation.OrderStatusTransitionValidator;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    public static final String CUSTOMER_NOT_FOUND_MSG = "Customer with customerId %d not found";

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    private static final ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreCase()
        .withIgnoreNullValues();

    @Override
    public NewOrderDto createOrder(NewOrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        Long customerId = orderDto.customerId();

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> customerNotFound(customerId));

        order.setStatus(getInitialStatus(order));
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(getProductsFromDb(order));

        return orderMapper.toDto(orderRepository.save(order));
    }

    private static OrderStatus getInitialStatus(Order order) {
        return order.getPaymentMethod() == PaymentMethod.CASH ? OrderStatus.ACCEPTED : OrderStatus.CREATED;
    }

    private List<OrderItem> getProductsFromDb(Order order) {
        List<Long> productIds = order.getOrderItems().stream()
            .map(item -> item.getProduct().getId())
            .toList();

        Map<Long, Product> orderedProducts = productRepository.findAllById(productIds)
            .stream()
            .collect(toMap(Product::getId, Function.identity()));

        return order.getOrderItems().stream()
            .map(orderItem -> {
                Long productId = orderItem.getProduct().getId();
                Product productFromDb = orderedProducts.get(productId);

                if (productFromDb == null) {
                    throw new ValidationException("Invalid product id: " + productId);
                }

                productFromDb.subtractFromStock(orderItem.getQuantity());
                orderItem.setProduct(productFromDb);
                orderItem.setUnitPrice(productFromDb.getPrice());//No discount
                return orderItem;
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetailsDto> getOrderDetailsById(Long id) {
        return orderRepository.findById(id)
            .map(orderMapper::toOrderDetailsDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderInfoDto> getOrders(Pageable pageable, OrderSearchParams searchParams) {
        Example<Order> orderExample = Example.of(orderMapper.searchParamsToEntity(searchParams), matcher);
        return orderRepository.findAllWithCustomerId(orderExample, pageable);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public Optional<OrderDetailsDto> updateOrder(Long id, OrderDetailsDto updatedOrder) {
        return orderRepository.findById(id)
            .map(existingOrder -> {
                OrderStatusTransitionValidator.validate(existingOrder, updatedOrder.getStatus());
                Order order = orderMapper.toEntity(updatedOrder);
                return orderMapper.toOrderDetailsDto(orderRepository.save(order));
            });
    }

    @Override
    public Optional<OrderDetailsDto> updateOrderPartially(Long id, OrderDetailsDto patch) {
        return orderRepository.findById(id)
            .map(existingOrder -> {
                OrderStatus patchWithStatus = patch.getStatus();
                if (patchWithStatus != null) {
                    OrderStatusTransitionValidator.validate(existingOrder, patchWithStatus);
                }

                Order updatedOrder = orderMapper.partialUpdate(patch, existingOrder);
                return orderMapper.toOrderDetailsDto(orderRepository.save(updatedOrder));
            });
    }

    private static ResponseStatusException customerNotFound(Long customerId) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, CUSTOMER_NOT_FOUND_MSG.formatted(customerId));
    }
}
