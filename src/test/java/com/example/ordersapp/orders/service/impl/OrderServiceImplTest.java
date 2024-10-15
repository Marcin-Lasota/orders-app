package com.example.ordersapp.orders.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.ordersapp.orders.model.Customer;
import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.Product;
import com.example.ordersapp.orders.model.dto.NewOrderDto;
import com.example.ordersapp.orders.model.dto.OrderDetailsDto;
import com.example.ordersapp.orders.model.dto.OrderItemDto;
import com.example.ordersapp.orders.model.dto.ProductDto;
import com.example.ordersapp.orders.model.dto.mapper.ProductMapper;
import com.example.ordersapp.orders.model.enums.OrderStatus;
import com.example.ordersapp.orders.model.enums.PaymentMethod;
import com.example.ordersapp.orders.repository.CustomerRepository;
import com.example.ordersapp.orders.repository.OrderRepository;
import com.example.ordersapp.orders.repository.ProductRepository;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private static final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
    private final ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

    @Test
    void shouldCreateOrderWithValidDataAndReduceStockQuantity() {
        //given
        LocalDateTime today = LocalDateTime.now();
        NewOrderDto newOrderDto = createNewOrderDto(PaymentMethod.PAYPAL);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(createMockCustomer()));
        when(productRepository.findAllById(any())).thenReturn(createMockProducts());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        NewOrderDto createdOrder = orderService.createOrder(newOrderDto);

        //then
        assertThat(createdOrder.customerId()).isEqualTo(1L);
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(createdOrder.orderDate().getDayOfMonth()).isEqualTo(today.getDayOfMonth());
        assertThat(createdOrder.orderDate().getMonthValue()).isEqualTo(today.getMonthValue());
        assertThat(createdOrder.orderDate().getYear()).isEqualTo(today.getYear());

        ProductDto firstProduct = createdOrder.orderItems().getFirst().product();
        ProductDto secondProduct = createdOrder.orderItems().get(1).product();
        assertThat(firstProduct.id()).isEqualTo(1L);
        assertThat(secondProduct.id()).isEqualTo(2L);
        assertThat(firstProduct.name()).isEqualTo("test");
        assertThat(secondProduct.name()).isEqualTo("test2");
        assertThat(firstProduct.stockQuantity()).isEqualTo(13);
        assertThat(secondProduct.stockQuantity()).isEqualTo(7);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithAcceptedStatusWhenPaymentMethodIsCash() {
        //given
        NewOrderDto newOrderDto = createNewOrderDto(PaymentMethod.CASH);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(createMockCustomer()));
        when(productRepository.findAllById(any())).thenReturn(createMockProducts());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        NewOrderDto createdOrder = orderService.createOrder(newOrderDto);

        //then
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.ACCEPTED);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldCalculateValidTotalPriceWhenRetrievingOrderDetails() {
        //given
        NewOrderDto newOrderDto = createNewOrderDto(PaymentMethod.BLIK);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(createMockCustomer()));
        when(productRepository.findAllById(any())).thenReturn(createMockProducts());
        orderService.createOrder(newOrderDto);
        verify(orderRepository, times(1)).save(captor.capture());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(captor.getValue()));

        //when
        Optional<OrderDetailsDto> createdOrder = orderService.getOrderDetailsById(1L);

        //then
        assertThat(createdOrder).isPresent();
        OrderDetailsDto orderDetailsDto = createdOrder.get();
        assertThat(orderDetailsDto.getTotalItems()).isEqualTo(5);
        assertThat(orderDetailsDto.getTotalPrice()).isEqualTo(new BigDecimal("319.95"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidProductIds() {
        //given
        NewOrderDto newOrderDto = createInvalidNewOrderDto(PaymentMethod.CASH);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(createMockCustomer()));
        when(productRepository.findAllById(any())).thenReturn(createMockProducts());

        //when
        Exception exception = assertThrows(ValidationException.class,
            () -> orderService.createOrder(newOrderDto));

        //then
        assertThat(exception.getMessage()).isEqualTo("Invalid product id: 99");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidOrderStatusTransitions")
    void shouldThrowValidationErrorWhenInvalidOrderStatusTransition(OrderStatus initialStatus, OrderStatus newStatus) {
        //given
        Long orderId = 1L;
        OrderDetailsDto updatedOrder = new OrderDetailsDto(null, null,
            null, newStatus, null, null);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(initialStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        //when
        Exception exception = assertThrows(ValidationException.class, () -> orderService.updateOrderPartially(orderId, updatedOrder));

        //then
        String expectedMessage = "Invalid order status change %s -> %s"
            .formatted(initialStatus, newStatus);

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);

        verify(orderRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("provideValidOrderStatusTransitions")
    void shouldChangeOrderStatusWhenValidOrderStatusTransition(OrderStatus initialStatus, OrderStatus newStatus) {
        //given
        Long orderId = 1L;
        OrderDetailsDto updateOrderDto = new OrderDetailsDto(null, null,
            null, newStatus, null, null);

        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(initialStatus);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Optional<OrderDetailsDto> updatedOrder = orderService.updateOrderPartially(orderId, updateOrderDto);

        //then
        assertThat(updatedOrder).isPresent();
        OrderDetailsDto orderDetailsDto = updatedOrder.get();

        assertThat(orderDetailsDto.getStatus()).isEqualTo(newStatus);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    private static @NotNull NewOrderDto createNewOrderDto(PaymentMethod paymentMethod) {
        List<OrderItemDto> orderItemDtos = createMockOrderItemsDtos();
        return new NewOrderDto(null, 1L, orderItemDtos, null, paymentMethod, null);
    }

    private static @NotNull NewOrderDto createInvalidNewOrderDto(PaymentMethod paymentMethod) {
        List<OrderItemDto> orderItemDtos = createInvalidMockOrderItemsDtos();
        return new NewOrderDto(null, 1L, orderItemDtos, null, paymentMethod, null);
    }

    private static Customer createMockCustomer() {
        Customer customer = new Customer();

        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("test@test.com");
        customer.setAddress("Lipowa 15");
        customer.setCity("Warszawa");
        customer.setCountry("Polska");
        customer.setPostalCode("50-001");
        customer.setPhone("123456789");

        return customer;
    }

    private static List<Product> createMockProducts() {
        return createMockOrderItemsDtos().stream()
            .map(product -> productMapper.toEntity(product.product()))
            .toList();
    }

    private static @NotNull List<OrderItemDto> createMockOrderItemsDtos() {
        ProductDto productDto = new ProductDto(1L, "test", "desc", new BigDecimal("99.99"), 15);
        ProductDto productDto2 = new ProductDto(2L, "test2", "desc2", new BigDecimal("39.99"), 10);

        return List.of(
            new OrderItemDto(1L, productDto, 2, productDto.price()),
            new OrderItemDto(2L, productDto2, 3, productDto2.price())
        );
    }

    private static @NotNull List<OrderItemDto> createInvalidMockOrderItemsDtos() {
        ProductDto productDto = new ProductDto(99L, null, null, null, null);
        ProductDto productDto2 = new ProductDto(999L, null, null, null, null);

        return List.of(
            new OrderItemDto(1L, productDto, 2, productDto.price()),
            new OrderItemDto(2L, productDto2, 3, productDto2.price())
        );
    }

    static Stream<Arguments> provideInvalidOrderStatusTransitions() {
        return Stream.of(
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.CREATED),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.SENT),
            Arguments.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.CREATED),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.SENT),
            Arguments.of(OrderStatus.CANCELLED, OrderStatus.DELIVERED),
            Arguments.of(OrderStatus.ACCEPTED, OrderStatus.CREATED),
            Arguments.of(OrderStatus.SENT, OrderStatus.CREATED),
            Arguments.of(OrderStatus.SENT, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.CREATED, OrderStatus.SENT)
        );
    }

    static Stream<Arguments> provideValidOrderStatusTransitions() {
        return Stream.of(
            Arguments.of(OrderStatus.CREATED, OrderStatus.ACCEPTED),
            Arguments.of(OrderStatus.CREATED, OrderStatus.CANCELLED),
            Arguments.of(OrderStatus.ACCEPTED, OrderStatus.SENT),
            Arguments.of(OrderStatus.SENT, OrderStatus.DELIVERED)
        );
    }
}
