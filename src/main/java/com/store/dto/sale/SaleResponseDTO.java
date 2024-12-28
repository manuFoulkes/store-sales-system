package com.store.dto.sale;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.dto.saleDetail.SaleDetailResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SaleResponseDTO(
   Long id,
   LocalDate saleDate,
   BigDecimal totalAmount,
   CustomerResponseDTO customer,
   List<SaleDetailResponseDTO> saleDetails
) {}
