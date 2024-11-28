package com.store.dto.customer;

public record CustomerResponseDTO(
        Long id,
        String name,
        String lastName,
        String email
        // List<Sale> saleList; //TODO: check if its necessary
) {
}
