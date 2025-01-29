package com.store.mapper;

import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDTO toProductResponse(Product product);
}
