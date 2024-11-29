package com.store.service;

import com.store.entity.Customer;
import com.store.dto.customer.CustomerResponseDTO;
import com.store.exception.CustomerNotFoundException;
import com.store.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if(customers.isEmpty()) {
            throw new CustomerNotFoundException("No customers found");
        }

        List<CustomerResponseDTO> customersResponse = new ArrayList<>();

        for(Customer customer : customers) {
            CustomerResponseDTO customerResponse = new CustomerResponseDTO(
                    customer.getId(),
                    customer.getName(),
                    customer.getLastName(),
                    customer.getEmail()
            );

            customersResponse.add(customerResponse);
        }

        return customersResponse;
    }
}
