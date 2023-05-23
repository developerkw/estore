package com.developerkw.estore;

import com.developerkw.estore.model.Basket;
import com.developerkw.estore.model.ModelUtil;
import com.developerkw.estore.model.Product;
import com.developerkw.estore.repository.BasketRepository;
import com.developerkw.estore.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Slf4j
@Component
@Profile("test | default")
public class DataLoader {

    private final ProductRepository productRepository;

    private final BasketRepository basketRepository;

    public DataLoader(ProductRepository productRepository, BasketRepository basketRepository) {
        this.productRepository = productRepository;
        this.basketRepository = basketRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Going to populate some testing data");

        var product1 = ModelUtil.createProduct(1, "Samsung Fold 4", "Mobile", 328, new BigDecimal("13800"), Set.of("BUY_3_GET_1_FREE"));
        productRepository.save(product1);

        var basket = ModelUtil.createBasket("testuser", Set.of(product1));
        basketRepository.save(basket);

        log.info("Sample data loaded into the DB");
    }

}