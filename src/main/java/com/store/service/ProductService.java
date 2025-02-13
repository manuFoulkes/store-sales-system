package com.store.service;

import com.store.dto.product.ProductRequestDTO;
import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import com.store.exception.product.ProductAlreadyExistsException;
import com.store.exception.product.ProductNotFoundException;
import com.store.mapper.ProductMapper;
import com.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        return productMapper.toProductResponse(product);
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            throw new ProductNotFoundException("Products not found");
        }

        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for (Product product : products) {
            ProductResponseDTO productResponseDTO = productMapper.toProductResponse(product);
            productResponseDTOList.add(productResponseDTO);
        }

        return productResponseDTOList;
    }

    @Transactional
    public ProductResponseDTO createNewProduct(ProductRequestDTO productRequestDTO) {
        Optional<Product> existingProduct = productRepository.findByNameAndBrand(
                productRequestDTO.name(),
                productRequestDTO.brand()
        );

        if (existingProduct.isPresent()) {
            throw new ProductAlreadyExistsException("Product with name " + productRequestDTO.name() +
                    "and brand " + productRequestDTO.brand() + " already exists");
        }

        Product product = productMapper.toProduct(productRequestDTO);

        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " does not exists"));

        productMapper.updateProductFromDTO(productRequestDTO, product);

        product = productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " does not exists"));

        productRepository.delete(product);
    }
}
