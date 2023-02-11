package com.morgan.inventorymanagement;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Transaction {
    private Integer id;
    private String description;
    private Integer qtySold;
    private Double amount;
    private Integer remainingStock;
    private String transactionType;
    private Timestamp timestamp;
}
