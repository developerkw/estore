package com.developerkw.estore.model;

import java.math.BigDecimal;
import java.util.List;

public class ModelUtil {

    public static Product createProduct(long id, String name, String category, int stock, BigDecimal price, List<String> discounts) {
        var product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setStock(stock);
        product.setPrice(price);
        product.setDiscounts(discounts);
        return product;
    }

    public static BasketItem createBasket(String name, Product product, int quantity) {
        var basketItem = new BasketItem();
        basketItem.setUserName(name);
        basketItem.setProduct(product);
        basketItem.setQuantity(quantity);
        return basketItem;
    }
}
