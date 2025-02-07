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
import com.store.mapper.SaleMapper;
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

    @Mock
    private SaleMapper saleMapper;

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
                .id(existingSaleId)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getEmail()
        );

        SaleDetailResponseDTO saleDetailResponseDTO = new SaleDetailResponseDTO(
                product.getId(),
                product.getName(),
                saleDetail1.getQuantity(),
                saleDetail1.getPrice()
        );

        SaleResponseDTO expectedResponse = new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                customerResponseDTO,
                List.of(saleDetailResponseDTO),
                sale.getStatus()
        );


        when(saleRepository.findById(existingSaleId)).thenReturn(Optional.of(sale));
        when(saleMapper.toSaleResponse(sale)).thenReturn(expectedResponse);

        SaleResponseDTO actualResponse = saleService.getSaleById(existingSaleId);

        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.customer().name(), actualResponse.customer().name());
        assertEquals(expectedResponse.saleDetails().get(0).price(), actualResponse.saleDetails().get(0).price());

        verify(saleRepository).findById(existingSaleId);
        verify(saleMapper).toSaleResponse(sale);
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

        SaleDetail saleDetail = SaleDetail.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();

        Sale sale = Sale.builder()
                .id(1L)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(List.of(saleDetail))
                .build();

        List<Sale> saleList = List.of(sale);

        SaleResponseDTO saleResponseDTO = new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                new CustomerResponseDTO(customer.getId(),
                        customer.getName(),
                        customer.getLastName(),
                        customer.getEmail()),
                List.of(new SaleDetailResponseDTO(product.getId(),
                        product.getName(),
                        saleDetail.getQuantity(),
                        saleDetail.getPrice())),
                SaleStatus.ACTIVE
        );

        List<SaleResponseDTO> expectedSaleResponseList = List.of(saleResponseDTO);

        when(saleRepository.findAll()).thenReturn(saleList);
        when(saleMapper.toSaleResponseList(saleList)).thenReturn(expectedSaleResponseList);

        List<SaleResponseDTO> actualSaleResponseList = saleService.getAllSales();

        assertEquals(expectedSaleResponseList.size(), actualSaleResponseList.size());
        assertEquals(expectedSaleResponseList.get(0).customer().name(), actualSaleResponseList.get(0).customer().name());
        assertEquals(expectedSaleResponseList.get(0).saleDetails().get(0).productName(), actualSaleResponseList.get(0).saleDetails().get(0).productName());

        verify(saleRepository).findAll();
        verify(saleMapper).toSaleResponseList(saleList);
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


        SaleDetail saleDetail = SaleDetail.builder()
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();

        List<SaleDetail> saleDetails = List.of(saleDetail);

        Sale sale = Sale.builder()
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        SaleDetailRequestDTO detailRequest = new SaleDetailRequestDTO(1L,
                1,
                BigDecimal.valueOf(6000)
        );

        List<SaleDetailRequestDTO> detailsRequest = List.of(detailRequest);

        SaleRequestDTO saleRequestDTO = new SaleRequestDTO(1L, detailsRequest);

        SaleResponseDTO expectedResponse = new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                new CustomerResponseDTO(customer.getId(),
                        customer.getName(),
                        customer.getLastName(),
                        customer.getEmail()),
                List.of(new SaleDetailResponseDTO(product.getId(),
                        product.getName(),
                        saleDetail.getQuantity(),
                        saleDetail.getPrice())),
                SaleStatus.ACTIVE
        );

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(saleRepository.save(sale)).thenReturn(sale);
        when(saleMapper.toSaleResponse(sale)).thenReturn(expectedResponse);

        SaleResponseDTO actualResponse = saleService.createNewSale(saleRequestDTO);

        assertEquals(expectedResponse.totalAmount(), actualResponse.totalAmount());
        assertEquals(expectedResponse.customer().name(), actualResponse.customer().name());
        assertEquals(expectedResponse.saleDetails().get(0).productName(), actualResponse.saleDetails().get(0).productName());

        verify(customerRepository).findById(customer.getId());
        verify(productRepository).findById(product.getId());
        verify(saleRepository).save(sale);
        verify(saleMapper).toSaleResponse(sale);
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

        SaleDetail saleDetail = SaleDetail.builder()
                .product(product)
                .quantity(1)
                .price(BigDecimal.valueOf(6000))
                .build();

        List<SaleDetail> saleDetails = List.of(saleDetail);

        Sale sale = Sale.builder()
                .id(existingSaleId)
                .saleDate(LocalDate.now())
                .totalAmount(BigDecimal.valueOf(6000))
                .customer(customer)
                .saleDetails(saleDetails)
                .build();

        SaleResponseDTO expectedResponse = new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                sale.getTotalAmount(),
                new CustomerResponseDTO(customer.getId(),
                        customer.getName(),
                        customer.getLastName(),
                        customer.getEmail()),
                List.of(new SaleDetailResponseDTO(product.getId(),
                        product.getName(),
                        saleDetail.getQuantity(),
                        saleDetail.getPrice())),
                SaleStatus.CANCELED
        );

        when(saleRepository.findById(existingSaleId)).thenReturn(Optional.of(sale));
        when(saleMapper.toSaleResponse(sale)).thenReturn(expectedResponse);

        SaleResponseDTO actualResponse = saleService.cancelSale(existingSaleId);

        assertEquals(SaleStatus.CANCELED, actualResponse.status());
        assertEquals(16, product.getStock());
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
