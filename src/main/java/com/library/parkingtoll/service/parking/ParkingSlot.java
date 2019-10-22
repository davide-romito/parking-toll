package com.library.parkingtoll.service.parking;

import com.library.parkingtoll.service.parking.exception.ParkingSlotException;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * The class represents individual parking slot and stores slot number and its availability status
 */
@Getter
public class ParkingSlot {

    private final String slot;

    private boolean available;

    private LocalDateTime takenTime;

    public ParkingSlot(String slot, boolean available) {
        this.slot = slot;
        this.available = available;
    }

    public void takeSlot(LocalDateTime takenTime) throws ParkingSlotException {
        if (!this.available) {
            throw new ParkingSlotException("Parking Slot not available");
        }
        this.available = false;
        this.takenTime = takenTime;
    }

    public LocalDateTime leaveSlot() {
        this.available = true;
        LocalDateTime takenTime1 = this.takenTime;
        this.takenTime = null;
        return takenTime1;
    }
}
