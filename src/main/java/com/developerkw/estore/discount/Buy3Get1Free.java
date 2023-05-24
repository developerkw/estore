package com.developerkw.estore.discount;

import com.developerkw.estore.model.PurchaseDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Assume the discount will be calculated based on the discounted total price.
 */
public class Buy3Get1Free implements Discount {
    @Override
    public PurchaseDto apply(PurchaseDto purchase) {
        var quantity = purchase.getQuantity();

        if (quantity <= 3) {
            return purchase;
        }

        var chargeableQuantity  = quantity - (quantity / 3);
        var newDiscountedTotalPrice = new BigDecimal(chargeableQuantity)
            .multiply(purchase.getDiscountedTotalPrice().divide(new BigDecimal(quantity), 2, RoundingMode.HALF_UP));
        purchase.setDiscountedTotalPrice(newDiscountedTotalPrice);

        return purchase;
    }
}
