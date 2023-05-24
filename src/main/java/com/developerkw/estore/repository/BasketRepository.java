package com.developerkw.estore.repository;

import com.developerkw.estore.model.BasketItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BasketRepository extends CrudRepository<BasketItem, Long> {
    List<BasketItem> findByUserName(String userName);

    boolean existsByIdAndUserName(Long id, String name);
}
