package com.store.repository;

import com.store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.brand = :brand")
    Optional<Product> findByNameAndBrand(@Param("name") String name, @Param("brand") String brand);

}
