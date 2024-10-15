package com.example.ordersapp.orders.model.dto.mapper;

import com.example.ordersapp.orders.model.Order;
import com.example.ordersapp.orders.model.OrderItem;
import com.example.ordersapp.orders.model.dto.NewOrderDto;
import com.example.ordersapp.orders.model.dto.OrderDetailsDto;
import com.example.ordersapp.orders.model.dto.OrderSearchParams;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    Order toEntity(NewOrderDto orderDto);

    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    Order toEntity(OrderDetailsDto orderDto);

    @AfterMapping
    default void linkOrderItems(@MappingTarget Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null) {
            return;
        }
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
    }

    @Mapping(target = "customerId", source = "customer.id")
    NewOrderDto toDto(Order order);

    OrderDetailsDto toOrderDetailsDto(Order order);

    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Order partialUpdate(OrderDetailsDto orderDto, @MappingTarget Order order);

    @Mapping(target = "customer.id", source = "customerId")
    Order searchParamsToEntity(OrderSearchParams searchParams);
}
