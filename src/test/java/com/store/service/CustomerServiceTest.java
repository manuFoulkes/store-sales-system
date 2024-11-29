package com.store.service;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.entity.Customer;
import com.store.exception.CustomerNotFoundException;
import com.store.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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

    @Test
    void getCustomerById_ShouldThrowAnException_WhenCustomerNotExist() {
        Long nonExistingCustomerId = 1L;

        when(customerRepository.findById(nonExistingCustomerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(nonExistingCustomerId);
        });

        verify(customerRepository, times(1)).findById(nonExistingCustomerId);
    }

    @Test
    void getAllCustomers_ShouldReturnAListOfCustomers_WhenCustomersExists() {
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer("John", "Doe", "john.doe@gmail.com");
        Customer customer2 = new Customer("Martin", "Fowler", "m.fowler@gmail.com");

        customers.add(customer1);
        customers.add(customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponseDTO> customerResponseDTOS = customerService.getAllCustomers();

        assertEquals(customers.size(), customerResponseDTOS.size());
        assertEquals(customer1.getName(), customerResponseDTOS.get(0).name());
        assertEquals(customer2.getName(), customerResponseDTOS.get(1).name());
    }

    @Test
    void getAllCustomers_ShouldThrowAnException_WhenCustomersNotExists() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class, () -> {
           customerService.getAllCustomers();
        });

        verify(customerRepository, times(1)).findAll();
    }
}