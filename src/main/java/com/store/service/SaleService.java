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
import com.store.enums.SaleStatus;
import com.store.exception.customer.CustomerNotFoundException;
import com.store.exception.product.InsufficientStockException;
import com.store.exception.product.ProductNotFoundException;
import com.store.exception.sale.InvalidSaleStateException;
import com.store.exception.sale.MaxSalesPerDayException;
import com.store.exception.sale.SaleNotFoundException;
import com.store.mapper.CustomerMapper;
import com.store.mapper.SaleDetailMapper;
import com.store.mapper.SaleMapper;
import com.store.repository.CustomerRepository;
import com.store.repository.ProductRepository;
import com.store.repository.SaleRepository;
import jakarta.transaction.Transactional;
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
    private final CustomerMapper customerMapper;
    private final SaleDetailMapper saleDetailMapper;
    private final SaleMapper saleMapper;

    @Autowired
    public SaleService(
            SaleRepository saleRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            CustomerMapper customerMapper,
            SaleDetailMapper saleDetailMapper,
            SaleMapper saleMapper
    ) {
        this.saleRepository = saleRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.customerMapper = customerMapper;
        this.saleDetailMapper = saleDetailMapper;
        this.saleMapper = saleMapper;
    }

    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale with id " + id + " does not exists"));

        return saleMapper.toSaleResponse(sale);
    }

    public List<SaleResponseDTO> getAllSales() {
        List<Sale> saleList = saleRepository.findAll();

        return saleMapper.toSaleResponseList(saleList);
    }

    @Transactional
    public SaleResponseDTO createNewSale(SaleRequestDTO saleRequest) {
        int maxSalesPerDay = 3;
        Long customerId = saleRequest.customerId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " not found"));

        int customerSalesToday = saleRepository.countSalesByCustomerAndDate(customerId, LocalDate.now());

        if(customerSalesToday >= maxSalesPerDay) {
            throw new MaxSalesPerDayException("The customer has reached the maximum sales limit");
        }

        List<SaleDetailRequestDTO> detailRequests = saleRequest.saleDetails();
        List<SaleDetail> saleDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(SaleDetailRequestDTO detailRequest : detailRequests) {
            Product product = productRepository.findById(detailRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException("Product with id " + detailRequest.productId() + " not found"));

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

        return saleMapper.toSaleResponse(newSale);
    }

    @Transactional
    public SaleResponseDTO cancelSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException("Sale with id " + id + " not found"));

        if(sale.getStatus() == SaleStatus.CANCELED) {
            throw new InvalidSaleStateException("The sale with id " + id + "is already canceled");
        }

        for(SaleDetail detail : sale.getSaleDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }

        sale.setStatus(SaleStatus.CANCELED);

        saleRepository.save(sale);

        return saleMapper.toSaleResponse(sale);
    }
}
