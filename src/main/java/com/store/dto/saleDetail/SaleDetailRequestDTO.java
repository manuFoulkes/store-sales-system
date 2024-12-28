package com.store.dto.saleDetail;

import java.math.BigDecimal;

public record SaleDetailRequestDTO(
        Long productId,
        int quantity,
        BigDecimal price
) {}
