package com.developerkw.estore.discount;

public enum DiscountEnum {
    BUY_3_GET_1_FREE(new Buy3Get1Free()),
    BUY_1_50_PERCENT_OFF_THE_SECOND(new Buy1AndSecond50PercentOff()),
    OFF_20_PERCENT(new PercentOff20());
    private final Discount discount;

    DiscountEnum(Discount discount) {
        this.discount = discount;
    }

    public Discount getDiscount() {
        return discount;
    }
}
