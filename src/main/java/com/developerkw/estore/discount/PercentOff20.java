package com.developerkw.estore.discount;

import com.developerkw.estore.model.PurchaseDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Assume the discount will be calculated based on the discounted total price.
 */
public class PercentOff20 implements Discount {
    @Override
    public PurchaseDto apply(PurchaseDto purchase) {
        var newDiscountedTotalPrice = new BigDecimal(0.8)
            .multiply(purchase.getDiscountedTotalPrice());
        purchase.setDiscountedTotalPrice(newDiscountedTotalPrice.setScale(2, RoundingMode.HALF_UP));

        return purchase;
    }
}
