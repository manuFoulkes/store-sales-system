package com.store.service;

import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import com.store.exception.product.ProductNotFoundException;
import com.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getProductById_ShouldReturnAProduct_WhenProductExist() {
        Long existingProductId = 1L;
        Product existingProduct = Product.builder()
                .id(existingProductId)
                .name("T-Shirt")
                .brand("Levis")
                .price(50.0)
                .stock(20)
                .build();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));

        ProductResponseDTO productResponseDTO = productService.getProductById(existingProductId);

        assertEquals(productResponseDTO.id(), existingProduct.getId());
        assertEquals(productResponseDTO.name(), existingProduct.getName());
        assertEquals(productResponseDTO.brand(), existingProduct.getBrand());
    }

    @Test
    void getCustomerById_ShouldThrowAnException_WhenProductNotExist() {
        Long nonExistingProductId = 1L;

        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(nonExistingProductId);
        });

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }
}
