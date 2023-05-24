package com.developerkw.estore.discount;

import com.developerkw.estore.model.ProductDto;
import com.developerkw.estore.model.PurchaseDto;

import java.math.BigDecimal;

public class MockDtoCreator {

    public static PurchaseDto getMockPurchaseDto(int quantity, BigDecimal price) {
        var purchaseDto = new PurchaseDto();
        purchaseDto.setQuantity(quantity);
        purchaseDto.setProduct(getMockProductDto(price));
        var total = new BigDecimal(quantity).multiply(price);
        purchaseDto.setOriginalTotalPrice(total);
        purchaseDto.setDiscountedTotalPrice(total);
        return purchaseDto;
    }

    public static ProductDto getMockProductDto(BigDecimal price) {
        var productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Demo Product");
        productDto.setCategory("Mobile");
        productDto.setPrice(price);
        productDto.setStock(120);
        return productDto;
    }

}
