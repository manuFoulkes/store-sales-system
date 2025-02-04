package com.store.mapper;

import com.store.dto.saleDetail.SaleDetailResponseDTO;
import com.store.entity.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleDetailMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    SaleDetailResponseDTO toSaleDetailResponse(SaleDetail saleDetail);

    List<SaleDetailResponseDTO> toSaleDetailResponseList(List<SaleDetail> saleDetails);

}
