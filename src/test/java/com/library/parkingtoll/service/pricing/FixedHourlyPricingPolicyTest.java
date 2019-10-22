package com.library.parkingtoll.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedHourlyPricingPolicyTest {
    private static final int HOUR_RATE = 3;
    private static final int FIXED_AMOUNT = 5;
    private FixedHourlyPricingPolicy pricingPolicy;

    @BeforeEach
    void setUp() {
        pricingPolicy = FixedHourlyPricingPolicy.builder().hourPrice(HOUR_RATE).fixedAmount(FIXED_AMOUNT).build();
    }

    @Test
    void testInvoiceLessThanOneMinute() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 11, 15, 10);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 11, 15, 30);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(FIXED_AMOUNT + HOUR_RATE, actualPrice);
    }

    @Test
    void testInvoiceLessThanOneHour() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 11, 15);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 11, 25);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(FIXED_AMOUNT + HOUR_RATE, actualPrice);
    }

    @Test
    void testInvoiceOneHour() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 14, 10);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 15, 10);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(FIXED_AMOUNT + HOUR_RATE, actualPrice);

    }

    @Test
    void testInvoiceTwoHours() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 18, 30);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 20, 30);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(FIXED_AMOUNT + 2 * HOUR_RATE, actualPrice);

    }

    @Test
    void testInvoiceTwoHoursAndFiveMinutes() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 10, 15);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 12, 20);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(FIXED_AMOUNT + 3 * HOUR_RATE, actualPrice);
    }

}