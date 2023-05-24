package com.developerkw.estore.discount;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PercentOff20Test {

    private final PercentOff20 discount = new PercentOff20();

    @ParameterizedTest
    @MethodSource("expectedTotalByQuantityAndPrice")
    void apply(int quantity, BigDecimal price, BigDecimal expected) {
        var purchaseDto = MockDtoCreator.getMockPurchaseDto(quantity, price);
        var result = discount.apply(purchaseDto);
        assertEquals(expected, result.getDiscountedTotalPrice());
    }

    private static Stream<Arguments> expectedTotalByQuantityAndPrice() {
        return Stream.of(
            Arguments.of(1, new BigDecimal("399.99"), new BigDecimal("319.99")),
            Arguments.of(2, new BigDecimal("399.99"), new BigDecimal("639.98")),
            Arguments.of(5, new BigDecimal("399.99"), new BigDecimal("1599.96"))
        );
    }
}
