package com.example.ordersapp.orders.service.impl;

import com.example.ordersapp.orders.model.Product;
import com.example.ordersapp.orders.model.dto.ProductDto;
import com.example.ordersapp.orders.model.dto.ProductSearchParams;
import com.example.ordersapp.orders.model.dto.mapper.ProductMapper;
import com.example.ordersapp.orders.repository.ProductRepository;
import com.example.ordersapp.orders.service.ProductService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    private static final ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreCase()
        .withIgnoreNullValues();

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);

        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
            .map(productMapper::toDto);
    }

    @Override
    public Page<ProductDto> getProducts(Pageable pageable, ProductSearchParams searchParams) {
        Example<Product> productExample = Example.of(productMapper.searchParamsToEntity(searchParams), matcher);
        return productRepository.findAll(productExample, pageable)
            .map(productMapper::toDto);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Optional<ProductDto> updateProduct(Long id, ProductDto updatedProduct) {
        return productRepository.findById(id)
            .map(existingProduct -> {
                Product product = productMapper.toEntity(updatedProduct);
                return productMapper.toDto(productRepository.save(product));
            });
    }

    @Override
    public Optional<ProductDto> updateProductPartially(Long id, ProductDto patch) {
        return productRepository.findById(id)
            .map(existingProduct -> {
                Product updatedProduct = productMapper.partialUpdate(patch, existingProduct);
                return productMapper.toDto(productRepository.save(updatedProduct));
            });
    }
}
