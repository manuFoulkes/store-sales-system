package com.store.controller;

import com.store.dto.sale.SaleRequestDTO;
import com.store.dto.sale.SaleResponseDTO;
import com.store.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/sales")
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
        return new ResponseEntity<>(saleService.getAllSales(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> createNewSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        return new ResponseEntity<>(saleService.createNewSale(saleRequestDTO), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id) {
        return new ResponseEntity<>(saleService.cancelSale(id), HttpStatus.OK);
    }
}
