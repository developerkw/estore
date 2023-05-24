package com.developerkw.estore.discount;

import com.developerkw.estore.model.PurchaseDto;

public interface Discount {

    PurchaseDto apply(PurchaseDto purchase);
}
