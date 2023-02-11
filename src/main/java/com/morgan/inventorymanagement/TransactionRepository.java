package com.morgan.inventorymanagement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public interface TransactionRepository extends CrudRepository<Item, Long> {
}
