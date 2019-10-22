package com.library.parkingtoll.service.pricing;

import com.library.parkingtoll.service.pricing.exception.PricingPolicyException;
import org.springframework.util.CollectionUtils;

import java.util.Map;

public class PricingPolicyFactory {

    private static final String FIX_PRICE = "FIX_PRICE";
    private static final String HOUR_PRICE = "HOUR_PRICE";

    private PricingPolicyFactory() {
    }

    public static PricingPolicy createPricingFactory(String pricingType, Map<String, Float> prices) throws PricingPolicyException {
        if (CollectionUtils.isEmpty(prices)) {
            throw new PricingPolicyException("Not valued prices for Pricing Policy implemented");
        }
        PricingPolicy pricingPolicy;
        switch (pricingType) {
            case "FIX_PLUS_HOURLY":
                pricingPolicy = FixedHourlyPricingPolicy.builder()
                        .fixedAmount(prices.get(FIX_PRICE))
                        .hourPrice(prices.get(HOUR_PRICE))
                        .build();
                break;
            case "HOURLY":
                pricingPolicy = HourlyPricingPolicy.builder()
                        .hourPrice(prices.get(HOUR_PRICE))
                        .build();
                break;
            default:
                throw new PricingPolicyException("Pricing Policy not implemented yet");
        }
        return pricingPolicy;
    }
}
