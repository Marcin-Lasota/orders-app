package com.example.ordersapp.orders.service;

import com.example.ordersapp.orders.model.dto.ProductDto;
import com.example.ordersapp.orders.model.dto.ProductSearchParams;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductDto createProduct(ProductDto order);

    Optional<ProductDto> getProductById(Long id);

    Page<ProductDto> getProducts(Pageable pageable, ProductSearchParams searchParams);

    void deleteProduct(Long id);

    Optional<ProductDto> updateProduct(Long id, ProductDto updatedProduct);

    Optional<ProductDto> updateProductPartially(Long id, ProductDto updatedOrder);
}
