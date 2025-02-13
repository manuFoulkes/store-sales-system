package com.store.service;

import com.store.repository.ProductRepository;
import com.store.service.csv.CsvImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CsvImportServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CsvImportService csvImportService;

    @Test
    void saveProductFromCsv_ShouldSaveProducts_IfCsvIsValid() {
        String csvContent = "name,brand,price,stock\nLaptop,HP,1200.50,10\nMouse,Logitech,25.99,50";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                csvContent.getBytes()
        );

        try {
            csvImportService.saveProductsFromCsv(file, true);
        } catch(Exception e) {
            System.err.println("Error in unit test 'saveProductFromCsv_ShouldSaveProducts_IfCsvIsValid': " + e);
        }

        verify(productRepository, times(2)).saveAll(anyList());
    }
}
