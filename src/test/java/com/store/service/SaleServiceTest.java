package com.store.service;

import com.store.dto.sale.SaleResponseDTO;
import com.store.entity.Customer;
import com.store.entity.Product;
import com.store.entity.Sale;
import com.store.entity.SaleDetail;
import com.store.exception.sale.SaleNotFoundException;
import com.store.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    void setUp() {}

    @Test
    void GetSaleById_ShouldReturnASale_WhenSaleExist() {
        Long existingSaleId = 1L;

        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        //Product: id, name, brand, price, stock
        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        //SaleDetail: id,product,quantity,price
        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);

        //Sale: id, saleDate, totalAmount, customer, saleDetails
        Sale sale = Sale.builder()
                .id(1L)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        when(saleRepository.findById(existingSaleId)).thenReturn(Optional.of(sale));

        SaleResponseDTO saleResponseDTO = saleService.getSaleById(existingSaleId);

        assertEquals(saleResponseDTO.id(), sale.getId());
        assertEquals(saleResponseDTO.customer().name(), sale.getCustomer().getName());
        assertEquals(saleResponseDTO.saleDetails().get(0).price(), sale.getSaleDetails().get(0).getPrice());

        verify(saleRepository).findById(existingSaleId);
    }

    @Test
    void GetSaleById_ShouldThrowAnException_WhenSaleNotExist() {
        Long nonExistingSale = 1L;

        when(saleRepository.findById(nonExistingSale)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () ->
                saleService.getSaleById(nonExistingSale));

        verify(saleRepository, times(1)).findById(nonExistingSale);
    }
}
