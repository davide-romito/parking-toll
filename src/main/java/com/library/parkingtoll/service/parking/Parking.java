package com.library.parkingtoll.service.parking;

import com.library.parkingtoll.CarType;
import com.library.parkingtoll.service.pricing.PricingPolicy;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class Parking {
    private final String parkingId;
    private final PricingPolicy pricingPolicy;
    private Map<CarType, Set<ParkingSlot>> slotsByType;

    public Parking(String parkingId, PricingPolicy pricingPolicy) {
        if (StringUtils.isEmpty(parkingId)) {
            this.parkingId = UUID.randomUUID().toString();
        } else {
            this.parkingId = parkingId;
        }
        this.pricingPolicy = pricingPolicy;
        this.slotsByType = new EnumMap<>(CarType.class);
    }

    public void addParkingSlotByType(CarType type, Integer numberOfParkingSlot) {
        Set<ParkingSlot> parkingSlots = new HashSet<>(numberOfParkingSlot);
        for (int i = 1; i <= numberOfParkingSlot; i++) {
            parkingSlots.add(new ParkingSlot(String.valueOf(slotsByType.values().stream().mapToLong(Collection::size).sum() + i), true));
        }
        this.slotsByType.put(type, parkingSlots);
    }

    public Set<ParkingSlot> getSlotsByType(CarType type) {
        return slotsByType.get(type);
    }
}
