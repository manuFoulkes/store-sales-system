package com.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.store"})
public class StoreSalesSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreSalesSystemApplication.class, args);
		System.out.println("RUNNING STORE_SALES_SYSTEM");
	}
}
