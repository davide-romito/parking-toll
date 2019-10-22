package com.library.parkingtoll.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HourlyPricingPolicyTest {

    private static final int HOUR_RATE = 3;
    private HourlyPricingPolicy pricingPolicy;

    @BeforeEach
    void setUp() {
        pricingPolicy = HourlyPricingPolicy.builder().hourPrice(HOUR_RATE).build();
    }

    @Test
    void testInvoiceLessThanOneMinute() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 11, 15, 10);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 11, 15, 30);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(HOUR_RATE, actualPrice);
    }

    @Test
    void testInvoiceLessThanOneHour() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 11, 15);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 11, 25);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(HOUR_RATE, actualPrice);
    }

    @Test
    void testInvoiceOneHour() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 14, 10);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 15, 10);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(HOUR_RATE, actualPrice);

    }

    @Test
    void testInvoiceTwoHours() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 18, 30);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 20, 30);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(2 * HOUR_RATE, actualPrice);

    }

    @Test
    void testInvoiceTwoHoursAndFiveMinutes() {
        LocalDateTime start = LocalDateTime.of(2019, 12, 10, 10, 15);
        LocalDateTime end = LocalDateTime.of(2019, 12, 10, 12, 20);
        float actualPrice = pricingPolicy.invoice(start, end);
        assertEquals(3 * HOUR_RATE, actualPrice);
    }


}