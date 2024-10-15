package com.example.ordersapp.orders.controller;

import com.example.ordersapp.orders.model.dto.NewOrderDto;
import com.example.ordersapp.orders.model.dto.OrderDetailsDto;
import com.example.ordersapp.orders.model.dto.OrderInfoDto;
import com.example.ordersapp.orders.model.dto.OrderSearchParams;
import com.example.ordersapp.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crate new order")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping
    public ResponseEntity<NewOrderDto> createOrder(@Valid @RequestBody NewOrderDto orderDto) {
        NewOrderDto createdOrderDto = orderService.createOrder(orderDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdOrderDto.id())
            .toUri();

        return ResponseEntity.created(location).body(createdOrderDto);
    }

    @Operation(summary = "Get order details by id")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> getOrderById(@PathVariable Long id) {
        return orderService.getOrderDetailsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get page of orders sorted/filtered based on the query parameters.")
    @ApiResponse(responseCode = "200", description = "Returned requested page of orders")
    @GetMapping
    public ResponseEntity<Page<OrderInfoDto>> getOrders(
        @RequestParam(required = false, name = "page", defaultValue = "0") int page,
        @RequestParam(required = false, name = "size", defaultValue = "20") int size,
        @RequestParam(required = false, name = "sortField", defaultValue = "orderDate") String sortField,
        @RequestParam(required = false, name = "direction", defaultValue = "DESC") String direction,
        @ParameterObject OrderSearchParams searchParams) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<OrderInfoDto> orderPage = orderService.getOrders(pageable, searchParams);
        return ResponseEntity.ok(orderPage);
    }

    @Operation(summary = "Delete order by id")
    @ApiResponse(responseCode = "200", description = "Order deleted")
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update order by id")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDetailsDto updatedOrderDto) {
        return orderService.updateOrder(id, updatedOrderDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Partially update order by id")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    @PatchMapping("/{id}")
    public ResponseEntity<OrderDetailsDto> updateOrderPartially(@PathVariable Long id, @RequestBody OrderDetailsDto patch) {
        return orderService.updateOrderPartially(id, patch)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
