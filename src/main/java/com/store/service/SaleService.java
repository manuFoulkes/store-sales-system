package com.store.service;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.dto.sale.SaleResponseDTO;
import com.store.dto.saleDetail.SaleDetailResponseDTO;
import com.store.entity.Customer;
import com.store.entity.Sale;
import com.store.entity.SaleDetail;
import com.store.exception.sale.SaleNotFoundException;
import com.store.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    // TODO: Implement MapStruct
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale with id " + id + " does not exists"));

        CustomerResponseDTO customerResponseDTO = getCustomerResponseDTO(sale);

        List<SaleDetailResponseDTO> saleDetailsResponse = getSaleDetailResponseDTOS(sale);

        return new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                customerResponseDTO,
                saleDetailsResponse
        );
    }

    public List<SaleResponseDTO> getAllSales() {
        List<Sale> saleList = saleRepository.findAll();
        List<SaleResponseDTO> saleResponseDTOList = new ArrayList<>();

        for(Sale sale : saleList) {
            SaleResponseDTO saleResponseDTO = new SaleResponseDTO(
                    sale.getId(),
                    sale.getSaleDate(),
                    sale.getTotalAmount(),
                    getCustomerResponseDTO(sale),
                    getSaleDetailResponseDTOS(sale)
            );
            saleResponseDTOList.add(saleResponseDTO);
        }

        return saleResponseDTOList;
    }

    private CustomerResponseDTO getCustomerResponseDTO(Sale sale) {
        Customer customer = sale.getCustomer();

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

    private List<SaleDetailResponseDTO> getSaleDetailResponseDTOS(Sale sale) {
        List<SaleDetail> saleDetails = sale.getSaleDetails();
        List<SaleDetailResponseDTO> saleDetailsResponse = new ArrayList<>();

        for(SaleDetail saleDetail : saleDetails) {
            SaleDetailResponseDTO saleDetailResponseDTO = new SaleDetailResponseDTO(
                    saleDetail.getId(),
                    saleDetail.getProduct().getName(),
                    saleDetail.getQuantity(),
                    saleDetail.getPrice()
            );

            saleDetailsResponse.add(saleDetailResponseDTO);
        }
        return saleDetailsResponse;
    }
}
