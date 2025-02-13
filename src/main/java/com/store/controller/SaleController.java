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
        List<SaleResponseDTO> saleResponseDTOList = saleService.getAllSales();

        return ResponseEntity.ok(saleResponseDTOList);
    }

    @PostMapping
    public ResponseEntity<SaleResponseDTO> createNewSale(@RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO newSale = saleService.createNewSale(saleRequestDTO);

        return ResponseEntity.ok(newSale);
    }

    @PutMapping("{id}")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id) {
        SaleResponseDTO cancelSale = saleService.cancelSale(id);

        return ResponseEntity.ok(cancelSale);
    }
}
