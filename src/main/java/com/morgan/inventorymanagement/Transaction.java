package com.morgan.inventorymanagement;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue
    private Integer transactionId;
    private Integer id;
    private String description;
    private Integer qtySold;
    private Double amount;
    private Integer remainingStock;
    private String transactionType;
    private Timestamp timestamp;

    public Transaction(Integer id, String description, Integer qtySold, Double amount, Integer remainingStock, String transactionType, Timestamp timestamp) {
        this.id = id;
        this.description = description;
        this.qtySold = qtySold;
        this.amount = amount;
        this.remainingStock = remainingStock;
        this.transactionType = transactionType;
        this.timestamp = timestamp;
    }
}
