package com.store.dto.saleDetail;

import java.math.BigDecimal;

public record SaleDetailResponseDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal price
) {}
