package com.store.service;

import com.store.dto.customer.CustomerRequestDTO;
import com.store.entity.Customer;
import com.store.dto.customer.CustomerResponseDTO;
import com.store.exception.customer.CustomerAlreadyExistsException;
import com.store.exception.customer.CustomerNotFoundException;
import com.store.mapper.CustomerMapper;
import com.store.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));

        return customerMapper.toCustomerResponse(customer);
    }

    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        if(customers.isEmpty()) {
            throw new CustomerNotFoundException("No customers found");
        }

        List<CustomerResponseDTO> customersResponse = new ArrayList<>();

        for(Customer customer : customers) {
            CustomerResponseDTO customerResponse = customerMapper.toCustomerResponse(customer);
            customersResponse.add(customerResponse);
        }

        return customersResponse;
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        customerRepository.getCustomerByEmail(customerRequestDTO.email())
                .ifPresent(customer -> {
                    throw new CustomerAlreadyExistsException("Customer with email " + customerRequestDTO.email() + " already exists");
                });

        Customer customer = new Customer();
        customer.setName(customerRequestDTO.name());
        customer.setLastName(customerRequestDTO.lastName());
        customer.setEmail(customerRequestDTO.email());

        customer = customerRepository.save(customer);

        return customerMapper.toCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));

        customer.setName(customerRequestDTO.name());
        customer.setLastName(customerRequestDTO.lastName());
        customer.setEmail(customerRequestDTO.email());

        customerRepository.save(customer);

        return customerMapper.toCustomerResponse(customer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));

        customerRepository.delete(customer);
    }
}
