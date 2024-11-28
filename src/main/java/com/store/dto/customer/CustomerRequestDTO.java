package com.store.dto.customer;

public record CustomerRequestDTO(
        String name,
        String lastName,
        String email
) {
}
