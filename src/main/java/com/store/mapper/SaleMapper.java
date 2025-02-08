package com.store.mapper;

import com.store.dto.sale.SaleResponseDTO;
import com.store.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, SaleDetailMapper.class})
public interface SaleMapper {

    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "saleDetails", source = "saleDetails")
    @Mapping(target = "status", source = "status")
    SaleResponseDTO toSaleResponse(Sale sale);

    List<SaleResponseDTO> toSaleResponseList(List<Sale> saleList);
}
