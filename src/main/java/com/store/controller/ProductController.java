package com.store.controller;

import com.store.dto.product.ProductRequestDTO;
import com.store.dto.product.ProductResponseDTO;
import com.store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //TODO: Add @Positive annotation after @PathVariable and manage the exception
    @GetMapping("{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> productResponseDTOList = productService.getAllProducts();

        return ResponseEntity.ok(productResponseDTOList);
    }

    //TODO: Add @Valid annotation in param for validation
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createNewProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO newProduct = productService.createNewProduct(productRequestDTO);

        return ResponseEntity.ok(newProduct);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);

        return ResponseEntity.ok(updatedProduct);
    }

}
