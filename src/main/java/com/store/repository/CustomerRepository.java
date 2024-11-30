package com.store.repository;

import com.store.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM customer c WHERE c.email = :email")
    Optional<Customer> getCustomerByEmail(String email);
}
