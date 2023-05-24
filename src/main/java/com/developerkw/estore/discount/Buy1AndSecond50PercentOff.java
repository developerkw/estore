package com.developerkw.estore.discount;

import com.developerkw.estore.model.PurchaseDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Assume this one will be calculated based on the original price.
 */
public class Buy1AndSecond50PercentOff implements Discount {
    @Override
    public PurchaseDto apply(PurchaseDto purchase) {
        var quantity = purchase.getQuantity();

        if (quantity <= 1) {
            return purchase;
        }

        var quantityEntitledForDiscount = quantity / 2;
        var totalDiscount = new BigDecimal(quantityEntitledForDiscount)
            .multiply(purchase.getProduct().getPrice().divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP));
        var newDiscountedTotalPrice = purchase.getDiscountedTotalPrice().subtract(totalDiscount);
        purchase.setDiscountedTotalPrice(newDiscountedTotalPrice);

        return purchase;
    }
}
