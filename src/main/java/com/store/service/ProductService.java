package com.store.service;

import com.store.dto.product.ProductRequestDTO;
import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import com.store.exception.product.ProductAlreadyExistsException;
import com.store.exception.product.ProductNotFoundException;
import com.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock()
        );
    }

    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        if(products.isEmpty()) {
            throw new ProductNotFoundException("Products not found");
        }

        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for(Product product : products) {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                    product.getId(),
                    product.getName(),
                    product.getBrand(),
                    product.getPrice(),
                    product.getStock()
            );
            productResponseDTOList.add(productResponseDTO);
        }
        return productResponseDTOList;
    }

    public ProductResponseDTO createNewProduct(ProductRequestDTO productRequestDTO) {
       Optional<Product> existingProduct = productRepository.findByNameAndBrand(
               productRequestDTO.name(),
               productRequestDTO.brand()
       );

       if(existingProduct.isPresent()) {
           throw new ProductAlreadyExistsException("Product with name " + productRequestDTO.name() +
                                                    "and brand " + productRequestDTO.brand() + " already exists");
       }

       Product product = Product.builder()
               .name(productRequestDTO.name())
               .brand(productRequestDTO.brand())
               .price(productRequestDTO.price())
               .stock(productRequestDTO.stock())
               .build();

       product = productRepository.save(product);

       return new ProductResponseDTO(
               product.getId(),
               product.getName(),
               product.getBrand(),
               product.getPrice(),
               product.getStock()
       );
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " does not exists"));

        product.setName(productRequestDTO.name());
        product.setBrand(productRequestDTO.brand());
        product.setPrice(productRequestDTO.price());
        product.setStock(productRequestDTO.stock());

        product = productRepository.save(product);

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getStock()
        );
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " does not exists"));

        productRepository.delete(product);
    }
}
