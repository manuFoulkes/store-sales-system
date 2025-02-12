package com.store.controller;

import com.store.service.csv.CsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileUploadController {

    private final CsvImportService csvImportService;

    @Autowired
    public FileUploadController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadProductsFromCsv(@RequestParam("file") MultipartFile csvFile,
                                                        @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        try {
            csvImportService.saveProductsFromCsv(csvFile, hasHeader);
            return ResponseEntity.ok("File uploaded with success");
        } catch(Exception e) {
            return ResponseEntity.status(500).body("Error uploading file");
        }
    }
}
