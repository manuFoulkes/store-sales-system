package com.store.dto.product;

public record ProductResponseDTO(
        Long id,
        String name,
        String brand,
        double price,
        int stock
) {
}
