package com.library.parkingtoll.service.pricing;

import com.library.parkingtoll.service.pricing.exception.PricingPolicyException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PricingPolicyFactoryTest {

    private static final String FIX_PLUS_HOURLY = "FIX_PLUS_HOURLY";

    @Test
    void testCreateHourlyPricingPolicy() throws PricingPolicyException {
        PricingPolicy pricingPolicy = PricingPolicyFactory.createPricingFactory("HOURLY", Collections.singletonMap("HOUR_PRICE", 5f));
        assertTrue(pricingPolicy instanceof HourlyPricingPolicy);
        assertEquals(15, pricingPolicy.invoice(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 30))));
    }

    @Test
    void testCreateFixedHourlyPricingPolicy() throws PricingPolicyException {
        Map<String, Float> map = new HashMap<>();
        map.put("HOUR_PRICE", 3f);
        map.put("FIX_PRICE", 5f);
        PricingPolicy pricingPolicy = PricingPolicyFactory.createPricingFactory(FIX_PLUS_HOURLY, map);
        assertTrue(pricingPolicy instanceof FixedHourlyPricingPolicy);
    }

    @Test()
    void testCreateNotImplementedPricingPolicy() {
        assertThrows(PricingPolicyException.class, () -> PricingPolicyFactory.createPricingFactory("FIXED", Collections.singletonMap("HOURLY", 5f)));
    }

    @Test()
    void testCreateNotPricedPricingPolicy() {
        assertThrows(PricingPolicyException.class, () -> PricingPolicyFactory.createPricingFactory(FIX_PLUS_HOURLY, Collections.emptyMap()));
    }

    @Test()
    void testCreateNullPricedPricingPolicy() {
        assertThrows(PricingPolicyException.class, () -> PricingPolicyFactory.createPricingFactory(FIX_PLUS_HOURLY, null));
    }

}