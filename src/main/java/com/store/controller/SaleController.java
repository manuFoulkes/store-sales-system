package com.store.controller;

import com.store.dto.sale.SaleResponseDTO;
import com.store.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("{id}")
    public ResponseEntity<SaleResponseDTO> getSailById(@PathVariable Long id) {
        return new ResponseEntity<>(saleService.getSaleById(id), HttpStatus.FOUND);
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        List<SaleResponseDTO> saleResponseDTOList = saleService.getAllSales();

        return ResponseEntity.ok(saleResponseDTOList);
    }
}
