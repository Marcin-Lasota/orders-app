package com.example.ordersapp.orders.model.dto.mapper;

import com.example.ordersapp.orders.model.Product;
import com.example.ordersapp.orders.model.dto.ProductDto;
import com.example.ordersapp.orders.model.dto.ProductSearchParams;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface ProductMapper {

    Product toEntity(ProductDto productDto);

    ProductDto toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(ProductDto productDto, @MappingTarget Product product);

    Product searchParamsToEntity(ProductSearchParams searchParams);
}
