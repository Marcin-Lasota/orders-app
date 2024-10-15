package com.example.ordersapp.orders.controller;

import com.example.ordersapp.orders.model.dto.ProductDto;
import com.example.ordersapp.orders.model.dto.ProductSearchParams;
import com.example.ordersapp.orders.service.ProductService;
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

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Crate new product")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto createdProductDto = productService.createProduct(productDto);
        URI location = URI.create("/api/v1/products" + createdProductDto.id());
        return ResponseEntity.created(location).body(createdProductDto);
    }

    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get page of products sorted/filtered based on the query parameters.")
    @ApiResponse(responseCode = "200", description = "Returned requested page of products")
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProducts(
        @RequestParam(required = false, name = "page", defaultValue = "0") int page,
        @RequestParam(required = false, name = "size", defaultValue = "20") int size,
        @RequestParam(required = false, name = "sortField", defaultValue = "name") String sortField,
        @RequestParam(required = false, name = "direction", defaultValue = "DESC") String direction,
        @ParameterObject ProductSearchParams searchParams) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        Page<ProductDto> productPage = productService.getProducts(pageable, searchParams);
        return ResponseEntity.ok(productPage);
    }

    @Operation(summary = "Delete product by id")
    @ApiResponse(responseCode = "200", description = "Product deleted")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update product by id")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto updatedProductDto) {
        return productService.updateProduct(id, updatedProductDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Partially update product by id")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProductPartially(@PathVariable Long id, @RequestBody ProductDto patch) {
        return productService.updateProductPartially(id, patch)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
