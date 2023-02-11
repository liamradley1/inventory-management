package com.morgan.inventorymanagement;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
	public class InventoryManagementApplication implements ApplicationRunner {

	@Autowired
	InventoryManagementService inventoryManagementService;

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		inventoryManagementService.runInventoryManagementService();
	}
}
