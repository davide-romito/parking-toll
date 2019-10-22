package com.library.parkingtoll.service.pricing;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Pricing policy for bill based on a fixed amount and on each hour spent in the parking
 */
public class FixedHourlyPricingPolicy implements PricingPolicy {
    private final float hourPrice;
    private final float fixedAmount;

    @Builder
    private FixedHourlyPricingPolicy(float hourPrice, float fixedAmount) {
        this.hourPrice = hourPrice;
        this.fixedAmount = fixedAmount;
    }

    @Override
    public float invoice(LocalDateTime enterDateTime, LocalDateTime leaveDateTime) {
        Duration duration = Duration.between(enterDateTime, leaveDateTime);
        long numberOfHours = duration.toHours();
        if (duration.minusHours(numberOfHours).getSeconds() > 0) {
            numberOfHours++;
        }
        return fixedAmount + hourPrice * numberOfHours;
    }
}
