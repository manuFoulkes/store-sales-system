package com.store.service;

import com.store.dto.sale.SaleRequestDTO;
import com.store.dto.sale.SaleResponseDTO;
import com.store.dto.saleDetail.SaleDetailRequestDTO;
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
import com.store.repository.CustomerRepository;
import com.store.repository.ProductRepository;
import com.store.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//TODO: Fix methods names and refactor
@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleService saleService;

    @BeforeEach
    void setUp() {}

    @Test
    void getSaleById_ShouldReturnASale_WhenSaleExist() {
        Long existingSaleId = 1L;

        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);

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
    void getSaleById_ShouldThrowAnException_WhenSaleNotExist() {
        Long nonExistingSale = 1L;

        when(saleRepository.findById(nonExistingSale)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () ->
                saleService.getSaleById(nonExistingSale));

        verify(saleRepository, times(1)).findById(nonExistingSale);
    }

    @Test
    void getAllSales_ShouldReturnAListOfSales_WhenSalesExists() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);

        Sale sale = Sale.builder()
                .id(1L)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        List<Sale> saleList = new ArrayList<>();

        saleList.add(sale);

        when(saleRepository.findAll()).thenReturn(saleList);

        List<SaleResponseDTO> saleResponseDTOList = saleService.getAllSales();

        assertEquals(saleList.size(), saleResponseDTOList.size());
        assertEquals(saleList.get(0).getCustomer().getName(), saleResponseDTOList.get(0).customer().name());
    }

    @Test
    void getAllSales_ShouldReturnAnEmptyList_WhenSalesNotExists() {
        when(saleRepository.findAll()).thenReturn(Collections.emptyList());

        List<SaleResponseDTO> saleResponseDTOList = saleService.getAllSales();

        assertTrue(saleResponseDTOList.isEmpty());

        verify(saleRepository).findAll();
    }

    // TODO: Refactor
    @Test
    void createNewSale_ShouldReturnSaleResponseDTO_IfSuccess () {
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);


        Sale sale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();


        List<SaleDetailRequestDTO> detailsRequest = new ArrayList<>();
        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(1L,
                1,
                BigDecimal.valueOf(6000)
        );

        detailsRequest.add(detailRequest);

        SaleRequestDTO saleRequestDTO = new SaleRequestDTO(1L, detailsRequest);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(saleRepository.save(sale)).thenReturn(sale);

        SaleResponseDTO saleResponseDTO = saleService.createNewSale(saleRequestDTO);

        assertEquals(saleResponseDTO.totalAmount(), sale.getTotalAmount());
        assertEquals(saleResponseDTO.customer().name(), sale.getCustomer().getName());
        assertEquals(saleResponseDTO.saleDetails().get(0).productName(), sale.getSaleDetails().get(0).getProduct().getName());
    }

    @Test
    void createNewSale_ShouldThrowAnException_IfCustomerNotExist() {
        Long nonExistingCustomerId = 1L;
        List<SaleDetailRequestDTO> detailsRequest = new ArrayList<>();
        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(1L, 1, BigDecimal.valueOf(6000));
        detailsRequest.add(detailRequest);

        SaleRequestDTO saleRequest = new SaleRequestDTO(nonExistingCustomerId, detailsRequest);

        when(customerRepository.findById(nonExistingCustomerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> saleService.createNewSale(saleRequest),
                "Expected CustomerNotFoundException when customer does not exist"
        );

        verify(customerRepository).findById(nonExistingCustomerId);
        verifyNoInteractions(productRepository, saleRepository);
    }

    @Test
    void createNewSale_ShouldThrowAnException_IfProductDoesNotExist() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Long nonExistingProduct = 1L;
        List<SaleDetailRequestDTO> detailsRequest = new ArrayList<>();
        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(nonExistingProduct, 1, BigDecimal.valueOf(6000));
        detailsRequest.add(detailRequest);

        SaleRequestDTO saleRequest = new SaleRequestDTO(customer.getId(), detailsRequest);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(nonExistingProduct)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> saleService.createNewSale(saleRequest),
                "Expected ProductNotFoundException when product does not exist"
        );

        verify(customerRepository).findById(customer.getId());
        verify(productRepository).findById(nonExistingProduct);
    }

    @Test
    void createNewSale_ShouldThrowAnException_IfMaxSalesPerDayExceeded() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        List<SaleDetailRequestDTO> detailsRequest = new ArrayList<>();
        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(1L, 1, BigDecimal.valueOf(6000));

        detailsRequest.add(detailRequest);

        SaleRequestDTO saleRequestDTO = new SaleRequestDTO(1L, detailsRequest);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(saleRepository.countSalesByCustomerAndDate(customer.getId(), LocalDate.now())).thenReturn(4);

        assertThrows(MaxSalesPerDayException.class,
                () -> saleService.createNewSale(saleRequestDTO));
    }

    @Test
    void createNewSale_ShouldThrowAnException_IfStockIsInsufficient() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(0)
                .build();

        List<SaleDetailRequestDTO> detailsRequest = new ArrayList<>();
        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(1L,
                1,
                BigDecimal.valueOf(6000)
        );

        detailsRequest.add(detailRequest);

        SaleRequestDTO saleRequestDTO = new SaleRequestDTO(1L, detailsRequest);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> saleService.createNewSale(saleRequestDTO));
    }

    @Test
    void cancelSale_ShouldSucceed_IfSaleIsActive() {
        Long existingSaleId = 1L;

        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);


        Sale sale = Sale.builder()
                .id(existingSaleId)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        when(saleRepository.findById(existingSaleId)).thenReturn(Optional.of(sale));

        SaleResponseDTO saleResponse = saleService.cancelSale(existingSaleId);

        assertEquals(SaleStatus.CANCELED, saleResponse.saleStatus());
    }

    @Test
    void cancelSale_ShouldThrowAnException_IfSaleNotExist() {
        Long nonExistingSaleId = 1L;

        when(saleRepository.findById(nonExistingSaleId)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class,
                () -> saleService.cancelSale(nonExistingSaleId));

        verify(saleRepository).findById(nonExistingSaleId);
    }

    @Test
    void cancelSale_ShouldThrowAnException_IfSaleStatusIsCanceled() {
        Long existingSaleId = 1L;

        Customer customer = Customer.builder()
                .id(1L)
                .name("John")
                .lastName("Doe")
                .email("jd@gmail.com")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Blue Cheese")
                .brand("La Serenisima")
                .price(6000)
                .stock(15)
                .build();

        List<SaleDetail> saleDetails = new ArrayList<>();
        SaleDetail saleDetail1 = SaleDetail.builder()
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();
        saleDetails.add(saleDetail1);


        Sale sale = Sale.builder()
                .id(existingSaleId)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .status(SaleStatus.CANCELED)
                .build();

        when(saleRepository.findById(existingSaleId)).thenReturn(Optional.of(sale));

        assertThrows(InvalidSaleStateException.class,
                () -> saleService.cancelSale(existingSaleId));

        verify(saleRepository).findById(existingSaleId);
    }
}
