package com.developerkw.estore.discount;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Buy1AndSecond50PercentOffTest {

    private final Buy1AndSecond50PercentOff discount = new Buy1AndSecond50PercentOff();

    @ParameterizedTest
    @MethodSource("expectedTotalByQuantityAndPrice")
    void apply(int quantity, BigDecimal price, BigDecimal expected) {
        var purchaseDto = MockDtoCreator.getMockPurchaseDto(quantity, price);
        var result = discount.apply(purchaseDto);
        assertEquals(expected, result.getDiscountedTotalPrice());
    }

    private static Stream<Arguments> expectedTotalByQuantityAndPrice() {
        return Stream.of(
            Arguments.of(1, new BigDecimal("399.99"), new BigDecimal("399.99")),
            Arguments.of(2, new BigDecimal("399.99"), new BigDecimal("599.98")),
            Arguments.of(5, new BigDecimal("399.99"), new BigDecimal("1599.95"))
        );
    }
}
