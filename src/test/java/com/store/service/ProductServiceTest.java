package com.store.service;

import com.store.dto.product.ProductRequestDTO;
import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import com.store.exception.product.ProductAlreadyExistsException;
import com.store.exception.product.ProductNotFoundException;
import com.store.mapper.ProductMapper;
import com.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

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

        ProductResponseDTO expectedResponse = new ProductResponseDTO(
                1L,
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));
        when(productMapper.toProductResponse(existingProduct)).thenReturn(expectedResponse);

        ProductResponseDTO actualResponse = productService.getProductById(existingProductId);

        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.brand(), actualResponse.brand());

        verify(productMapper).toProductResponse(existingProduct);
    }

    @Test
    void getProductById_ShouldThrowAnException_WhenProductNotExist() {
        Long nonExistingProductId = 1L;

        when(productRepository.findById(nonExistingProductId)).thenReturn(empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(nonExistingProductId);
        });

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }

    @Test
    void getAllProducts_ShouldReturnAListOfProducts_IfCustomersExist() {
        List<Product> productList = List.of(
                new Product("T-Shirt", "Levis", 50.0, 20),
                new Product("Jean", "Levis", 65.0, 23)
        );

        List<ProductResponseDTO> expectedResponse = List.of(
                new ProductResponseDTO(1L, "T-Shirt", "Levis", 50.0, 20),
                new ProductResponseDTO(2L,"Jean", "Levis", 65.0, 23)
        );

        when(productRepository.findAll()).thenReturn(productList);
        when(productMapper.toProductResponse(productList.get(0))).thenReturn(expectedResponse.get(0));
        when(productMapper.toProductResponse(productList.get(1))).thenReturn(expectedResponse.get(1));

        List<ProductResponseDTO> actualResponse = productService.getAllProducts();

        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse.get(0).name(), actualResponse.get(0).name());
        assertEquals(expectedResponse.get(1).name(), actualResponse.get(1).name());
    }

    @Test
    void getAllProducts_ShouldThrowAnException_WhenProductsNotExist() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ProductNotFoundException.class, () -> {
           productService.getAllProducts();
        });

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void createNewProduct_ShouldSuccess_WhenProductNotExists() {
        Product product = Product.builder()
                .name("T-Shirt")
                .brand("Levis")
                .price(50.0)
                .stock(20)
                .build();

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        ProductResponseDTO expectedResponse = new ProductResponseDTO(
                1L,
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        when(productMapper.toProduct(productRequestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toProductResponse(product)).thenReturn(expectedResponse);

        ProductResponseDTO actualResponse = productService.createNewProduct(productRequestDTO);

        assertEquals(expectedResponse.name(), actualResponse.name());
        assertEquals(expectedResponse.brand(), actualResponse.brand());
        assertEquals(expectedResponse.price(), actualResponse.price());

        verify(productMapper).toProduct(productRequestDTO);
        verify(productMapper).toProductResponse(product);
    }

    @Test
    void createNewProduct_ShouldThrowAnException_WhenProductAlreadyExists() {
        Product existingProduct = Product.builder()
                .name("T-Shirt")
                .brand("Levis")
                .price(50.0)
                .stock(20)
                .build();

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        when(productRepository.findByNameAndBrand(productRequestDTO.name(), productRequestDTO.brand()))
                .thenReturn(Optional.of(existingProduct));

        assertThrows(ProductAlreadyExistsException.class, () ->
                productService.createNewProduct(productRequestDTO));

        verify(productRepository, never()).save(existingProduct);
    }

    @Test
    void updateProduct_ShouldSuccess_WhenProductExists() {
        Product existingProduct = Product.builder()
                .name("T-Shirt")
                .brand("Levis")
                .price(50.0)
                .stock(20)
                .build();

        ProductRequestDTO productRequestDTO = new ProductRequestDTO(
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductResponseDTO updatedProduct = productService.updateProduct(1L,productRequestDTO);

        assertEquals(updatedProduct.name(), productRequestDTO.name());
        assertEquals(updatedProduct.brand(), productRequestDTO.brand());
        assertEquals(updatedProduct.price(), productRequestDTO.price());
    }

    @Test
    void updateProduct_ShouldThrowAnException_WhenProductNotExists() {
        Long nonExistingProductId = 1L;
        ProductRequestDTO productRequestDTO =  new ProductRequestDTO(
                "T-Shirt",
                "Levis",
                50.0,
                20
        );

        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProduct(nonExistingProductId, productRequestDTO)
        );

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldSuccess_WhenProductExists() {
        Long existingProductId = 1L;
        Product existingProduct = Product.builder()
                .name("T-Shirt")
                .brand("Levis")
                .price(50.0)
                .stock(20)
                .build();

        when(productRepository.findById(existingProductId)).thenReturn(Optional.of(existingProduct));

        productService.deleteProduct(existingProductId);

        verify(productRepository, times(1)).delete(existingProduct);
    }

    @Test
    void deleteProduct_ShouldThrowAnException_WhenProductNotExists() {
        Long nonExistingProductId = 1L;

        when(productRepository.findById(nonExistingProductId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.deleteProduct(nonExistingProductId));

        verify(productRepository, times(1)).findById(nonExistingProductId);
    }
}