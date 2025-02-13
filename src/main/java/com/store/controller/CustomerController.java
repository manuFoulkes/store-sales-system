package com.store.controller;

import com.store.dto.customer.CustomerRequestDTO;
import com.store.dto.customer.CustomerResponseDTO;
import com.store.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //TODO: Add @Positive annotation after @PathVariable and manage the exception
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(@PathVariable Long id) {
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();

        return ResponseEntity.ok(customers);
    }

    //TODO: Add @Valid annotation in param for validation
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);

        return ResponseEntity
                .created(URI.create("/customers/" + createdCustomer.id()))
                .body(createdCustomer);
    }

    //TODO: Add @Valid annotation in param for validation
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerRequestDTO customerRequest) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerRequest);

        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
