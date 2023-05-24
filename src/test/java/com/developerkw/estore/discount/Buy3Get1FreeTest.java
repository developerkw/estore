package com.developerkw.estore.discount;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Buy3Get1FreeTest {

    private final Buy3Get1Free discount = new Buy3Get1Free();

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
            Arguments.of(3, new BigDecimal("399.99"), new BigDecimal("1199.97")),
            Arguments.of(4, new BigDecimal("399.99"), new BigDecimal("1199.97")),
            Arguments.of(11, new BigDecimal("399.99"), new BigDecimal("3199.92"))
        );
    }
}
