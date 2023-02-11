package com.morgan.inventorymanagement.dao;

import com.morgan.inventorymanagement.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
}
