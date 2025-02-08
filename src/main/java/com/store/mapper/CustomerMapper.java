package com.store.mapper;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.entity.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerResponseDTO toCustomerResponse(Customer customer);
}
