package com.store.dto.product;

public record ProductRequestDTO(
        String name,
        String brand,
        double price,
        int stock
) {
}
