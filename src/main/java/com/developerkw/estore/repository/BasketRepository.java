package com.developerkw.estore.repository;

import com.developerkw.estore.model.Basket;
import org.springframework.data.repository.CrudRepository;

public interface BasketRepository extends CrudRepository<Basket, String> {
}
