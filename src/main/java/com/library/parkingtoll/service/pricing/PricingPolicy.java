package com.library.parkingtoll.service.pricing;

import java.time.LocalDateTime;

public interface PricingPolicy {
    float invoice(LocalDateTime enterDateTime, LocalDateTime leaveDateTime);
}
