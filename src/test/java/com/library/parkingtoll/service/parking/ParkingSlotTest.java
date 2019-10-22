package com.library.parkingtoll.service.parking;

import com.library.parkingtoll.service.parking.exception.ParkingSlotException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingSlotTest {

    @Test
    void testTakeSlotAvailable() throws ParkingSlotException {
        ParkingSlot parkingSlot = new ParkingSlot("SLOT", true);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15));
        parkingSlot.takeSlot(dateTime);
        assertEquals(dateTime, parkingSlot.getTakenTime());
        assertFalse(parkingSlot.isAvailable());
    }

    @Test
    void testTakeSlotNotAvailable() {
        ParkingSlot parkingSlot = new ParkingSlot("SLOT", false);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15));
        assertThrows(ParkingSlotException.class, () -> parkingSlot.takeSlot(dateTime));
    }

    @Test
    void leaveSlot() throws ParkingSlotException {
        ParkingSlot parkingSlot = new ParkingSlot("SLOT", true);
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 15));
        parkingSlot.takeSlot(dateTime);
        assertEquals(dateTime, parkingSlot.leaveSlot());
        assertTrue(parkingSlot.isAvailable());
    }
}