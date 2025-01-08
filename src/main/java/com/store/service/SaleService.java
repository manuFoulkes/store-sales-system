package com.store.service;

import com.store.dto.customer.CustomerResponseDTO;
import com.store.dto.sale.SaleRequestDTO;
import com.store.dto.sale.SaleResponseDTO;
import com.store.dto.saleDetail.SaleDetailRequestDTO;
import com.store.dto.saleDetail.SaleDetailResponseDTO;
import com.store.entity.Customer;
import com.store.entity.Product;
import com.store.entity.Sale;
import com.store.entity.SaleDetail;
import com.store.exception.customer.CustomerNotFoundException;
import com.store.exception.product.ProductNotFoundException;
import com.store.exception.sale.SaleNotFoundException;
import com.store.repository.CustomerRepository;
import com.store.repository.ProductRepository;
import com.store.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
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

    public SaleResponseDTO createNewSale(SaleRequestDTO saleRequest) {
        int maxSalesPerDay = 3;
        Long customerId = saleRequest.customerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " not found"));

        // TODO: Create countSalesByCustomerAndDate method in CustomerRepository
        int customerSalesCount = customerRepository.countSalesByCustomerAndDate(customerId, LocalDate.now());

        // TODO: Create MaxSalesPerDayException class
        if(customerSalesCount >= maxSalesPerDay) {
            throw new MaxSalesPerDayException("The customer has reached the maximum sales limit");
        }

        List<SaleDetailRequestDTO> detailRequests = saleRequest.saleDetails();
        List<SaleDetail> saleDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(SaleDetailRequestDTO detailRequest : detailRequests) {
            Product product = productRepository.findById(detailRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product with id " + detailRequest.productId() + " not found"));

            // TODO: Create insufficientStockException class
            if(product.getStock() < detailRequest.quantity()) {
                throw new InsufficientStockException("Insufficient stock for product " + product.getName());
            }

            product.setStock(product.getStock() - detailRequest.quantity());
            productRepository.save(product);

            BigDecimal price = detailRequest.price();
            BigDecimal quantity = BigDecimal.valueOf(detailRequest.quantity());
            totalAmount = totalAmount.add(price.multiply(quantity));

            SaleDetail saleDetail = SaleDetail.builder()
                    .product(product)
                    .quantity(detailRequest.quantity())
                    .price(price)
                    .build();

            saleDetails.add(saleDetail);
        }

        Sale newSale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalAmount(totalAmount)
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        for(SaleDetail saleDetail : saleDetails) {
            saleDetail.setSale(newSale);
        }

        newSale = saleRepository.save(newSale);

        CustomerResponseDTO customerResponseDTO = getCustomerResponseDTO(newSale);
        List<SaleDetailResponseDTO> detailResponseDTOS = getSaleDetailResponseDTOS(newSale);

        return new SaleResponseDTO(
                newSale.getId(),
                newSale.getSaleDate(),
                newSale.getTotalAmount(),
                customerResponseDTO,
                detailResponseDTOS
        );
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
