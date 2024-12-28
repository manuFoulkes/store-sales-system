package com.store.dto.sale;

import com.store.dto.saleDetail.SaleDetailRequestDTO.java;
import java.util.List;

public record SaleRequestDTO(
        Long customerId,
        List<SaleDetailRequestDTO> saleDetails
) {}
