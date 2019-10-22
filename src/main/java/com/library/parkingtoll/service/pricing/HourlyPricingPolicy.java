package com.library.parkingtoll.service.pricing;

import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Pricing policy for bill based on each hour spent in the parking
 */
public class HourlyPricingPolicy implements PricingPolicy {
    private final float hourPrice;

    @Builder
    private HourlyPricingPolicy(float hourPrice) {
        this.hourPrice = hourPrice;
    }

    @Override
    public float invoice(LocalDateTime enterDateTime, LocalDateTime leaveDateTime) {
        Duration duration = Duration.between(enterDateTime, leaveDateTime);
        long numberOfHours = duration.toHours();
        if (duration.minusHours(numberOfHours).getSeconds() > 0) {
            numberOfHours++;
        }
        return hourPrice * numberOfHours;
    }
}
