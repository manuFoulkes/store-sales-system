package com.store.service;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.entity.Customer;
import com.store.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setup() {

    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExist() {
        Long customerId = 1L;
        Customer expectedCustomer = Customer.builder()
                .id(customerId)
                .name("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(expectedCustomer));

        CustomerResponseDTO customerResponseDTO = customerService.getCustomerById(customerId);

        assertEquals(expectedCustomer.getName(), customerResponseDTO.name());
    }
}
