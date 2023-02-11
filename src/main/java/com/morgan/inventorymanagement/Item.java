package com.morgan.inventorymanagement;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Item {
    @Id
    private Integer id;
    private String description;
    private Double unitPrice;
    private Integer qtyStock;
    private Double totalPrice;

    public Item(String description, Double unitPrice, Integer qtyStock, Double totalPrice) {
        this.description = description;
        this.unitPrice = unitPrice;
        this.qtyStock = qtyStock;
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
