package com.developerkw.estore.repository;

import com.developerkw.estore.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
