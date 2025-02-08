package com.store.mapper;

import com.store.dto.product.ProductRequestDTO;
import com.store.dto.product.ProductResponseDTO;
import com.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDTO toProductResponse(Product product);

    Product toProduct(ProductRequestDTO productRequest);

    void updateProductFromDTO(ProductRequestDTO productRequestDTO, @MappingTarget Product product);
}
