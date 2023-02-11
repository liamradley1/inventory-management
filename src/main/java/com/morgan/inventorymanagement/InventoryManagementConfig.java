package com.morgan.inventorymanagement;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class InventoryManagementConfig {

    @Value("${items.file.path}")
    private String itemsFilePath;

    @Value("${transactions.file.path}")
    private String transactionsFilePath;
}
