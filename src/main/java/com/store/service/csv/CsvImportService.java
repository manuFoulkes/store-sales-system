package com.store.service.csv;

import com.opencsv.CSVReader;
import com.store.entity.Product;
import com.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvImportService {

    private final ProductRepository productRepository;

    @Autowired
    public CsvImportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void saveProductsFromCsv(MultipartFile csvFile, boolean hasHeader) throws Exception {
        try(Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
            CSVReader csvReader = new CSVReader(reader)) {

            List<Product> productList = new ArrayList<>();
            List<String[]> lines = csvReader.readAll();

            if(hasHeader && !lines.isEmpty()) {
                lines.removeFirst();
            }

            for(String[] line : lines) {
                if(line.length < 4) {
                    System.err.println("Invalid row: " + Arrays.toString(line));
                    continue;
                }

                try {
                    Product product = new Product(
                            line[0],
                            line[1],
                            Double.parseDouble(line[2]),
                            Integer.parseInt(line[3])
                    );
                    productList.add(product);
                } catch(NumberFormatException e) {
                    System.err.println("Error to parse row: " + Arrays.toString(line) + " -> " + e.getMessage());
                }

                if(!productList.isEmpty()) {
                    productRepository.saveAll(productList);
                }
            }
        } catch(IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }
}
