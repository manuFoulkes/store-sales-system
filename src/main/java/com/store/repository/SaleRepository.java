package com.store.repository;

import com.store.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.customer.id = :customerId AND s.saleDate = :saleDate")
    int countSalesByCustomerAndDate(@Param("customerId") Long customerId, @Param("saleDate")LocalDate saleDate);

}
