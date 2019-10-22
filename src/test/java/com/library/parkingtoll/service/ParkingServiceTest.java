package com.library.parkingtoll.service;

import com.library.parkingtoll.CarType;
import com.library.parkingtoll.model.CarRequest;
import com.library.parkingtoll.model.CarResponse;
import com.library.parkingtoll.model.ParkingRequest;
import com.library.parkingtoll.model.ParkingResponse;
import com.library.parkingtoll.model.ParkingSlotRequest;
import com.library.parkingtoll.model.PriceRequest;
import com.library.parkingtoll.model.PriceResponse;
import com.library.parkingtoll.service.parking.Parking;
import com.library.parkingtoll.service.parking.ParkingSlot;
import com.library.parkingtoll.service.parking.exception.ParkingAlreadyExistException;
import com.library.parkingtoll.service.parking.exception.ParkingNotFoundException;
import com.library.parkingtoll.service.parking.exception.ParkingSlotException;
import com.library.parkingtoll.service.pricing.HourlyPricingPolicy;
import com.library.parkingtoll.service.pricing.exception.PricingPolicyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingServiceTest {
    private static final String PARKING_NAME = "PARKING_NAME";
    private static final String HOURLY = "HOURLY";
    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
        parkingService = new ParkingService();
    }

    @Test
    void createParking() throws PricingPolicyException, ParkingAlreadyExistException {
        ParkingRequest parkingRequest = buildParkingRequest(PARKING_NAME, HOURLY, 10, 5, 1);
        ParkingResponse parkingResponse = parkingService.createParking(parkingRequest);

        assertEquals(PARKING_NAME, parkingResponse.getParkingId());
    }

    @Test
    void createParkingWithoutName() throws PricingPolicyException, ParkingAlreadyExistException {
        ParkingRequest parkingRequest = buildParkingRequest(null, HOURLY, 25, 2, 2);
        ParkingResponse parkingResponse = parkingService.createParking(parkingRequest);

        assertNotNull(parkingResponse.getParkingId());
    }

    @Test
    void createParkingWithNameAlreadyPresent() {
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build()));
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        ParkingRequest parkingRequest = buildParkingRequest(PARKING_NAME, HOURLY, 100, 50, 10);

        assertThrows(ParkingAlreadyExistException.class, () -> parkingService.createParking(parkingRequest));
    }

    @Test
    void createParkingWithPricingPolicyNotImplemented() {
        ParkingRequest parkingRequest = buildParkingRequest(null, "MINUTE", 10, 5, 1);

        assertThrows(PricingPolicyException.class, () -> parkingService.createParking(parkingRequest));
    }

    @Test
    void parkCar() throws ParkingNotFoundException, ParkingSlotException {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 5);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        CarRequest carRequest = new CarRequest();
        carRequest.setType(CarType.STANDARD);
        CarResponse carResponse = parkingService.parkCar(PARKING_NAME, carRequest);

        assertNotNull(carResponse.getSlotId());
        assertNotNull(carResponse.getOccupationTime());
        assertEquals(CarType.STANDARD, carResponse.getType());
    }

    @Test
    void parkCarCheckEVType() throws ParkingNotFoundException, ParkingSlotException {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.ELECTRIC_POWERED_20_KW, 2);
        parking.addParkingSlotByType(CarType.ELECTRIC_POWERED_50_KW, 2);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        CarRequest carRequest = new CarRequest();
        carRequest.setType(CarType.ELECTRIC_POWERED_20_KW);
        parkingService.parkCar(PARKING_NAME, carRequest);

        assertTrue(parking.getSlotsByType().get(CarType.ELECTRIC_POWERED_50_KW).stream().allMatch(ParkingSlot::isAvailable));
        assertEquals(1, parking.getSlotsByType().get(CarType.ELECTRIC_POWERED_20_KW).stream().filter(ParkingSlot::isAvailable).count());
        assertEquals(1, parking.getSlotsByType().get(CarType.ELECTRIC_POWERED_20_KW).stream().filter(parkingSlot -> !parkingSlot.isAvailable()).count());
    }

    @Test
    void parkCarNotExistingParking() {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 5);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        CarRequest carRequest = new CarRequest();
        carRequest.setType(CarType.STANDARD);

        assertThrows(ParkingNotFoundException.class, () -> parkingService.parkCar("FAKE_PARKING", carRequest));
    }

    @Test
    void parkCarNotAvailableSlot() {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 0);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        CarRequest carRequest = new CarRequest();
        carRequest.setType(CarType.STANDARD);

        assertThrows(ParkingSlotException.class, () -> parkingService.parkCar(PARKING_NAME, carRequest));
    }

    @Test
    void takeCar() throws ParkingSlotException, ParkingNotFoundException {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().hourPrice(2).build());
        parking.addParkingSlotByType(CarType.STANDARD, 5);
        LocalDateTime arrivalTime = LocalDateTime.now().minusMinutes(50);
        for (ParkingSlot parkingSlot : parking.getSlotsByType(CarType.STANDARD)) {
            parkingSlot.takeSlot(arrivalTime);
        }
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        PriceResponse priceResponse = parkingService.takeCar(PARKING_NAME, "1");

        assertEquals(arrivalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), priceResponse.getArrivalTime());
        assertEquals(2, priceResponse.getPricing());
    }

    @Test
    void takeCarNotExistingParking() {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 3);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        assertThrows(ParkingNotFoundException.class, () -> parkingService.takeCar("FAKE_PARKING", "2"));
    }

    @Test
    void takeCarNotExistingSlot() {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 3);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        assertThrows(ParkingSlotException.class, () -> parkingService.takeCar(PARKING_NAME, "10"));
    }

    @Test
    void takeCarNotOccupiedSlot() {
        Parking parking = new Parking(PARKING_NAME, HourlyPricingPolicy.builder().build());
        parking.addParkingSlotByType(CarType.STANDARD, 3);
        Map<String, Parking> parkingMap = Collections.singletonMap(PARKING_NAME, parking);
        ReflectionTestUtils.setField(parkingService, "map", parkingMap);

        assertThrows(ParkingSlotException.class, () -> parkingService.takeCar(PARKING_NAME, "1"));
    }

    private ParkingRequest buildParkingRequest(String parkingName, String pricingPolicy, int standardSlot, int ev20kwSlot, int ev50kwSlot) {
        ParkingRequest parkingRequest = new ParkingRequest();
        parkingRequest.setParkingName(parkingName);
        parkingRequest.setPricingPolicy(pricingPolicy);
        PriceRequest price = new PriceRequest();
        price.setHourRate(5F);
        parkingRequest.setPrice(price);
        ParkingSlotRequest parkingSlotRequestStandard = new ParkingSlotRequest();
        parkingSlotRequestStandard.setType(CarType.STANDARD);
        parkingSlotRequestStandard.setAvailableSpot(standardSlot);
        ParkingSlotRequest parkingSlotRequestEV20KW = new ParkingSlotRequest();
        parkingSlotRequestEV20KW.setType(CarType.ELECTRIC_POWERED_20_KW);
        parkingSlotRequestEV20KW.setAvailableSpot(ev20kwSlot);
        ParkingSlotRequest parkingSlotRequestEV50KW = new ParkingSlotRequest();
        parkingSlotRequestEV50KW.setType(CarType.ELECTRIC_POWERED_50_KW);
        parkingSlotRequestEV50KW.setAvailableSpot(ev50kwSlot);
        parkingRequest.setParkingSlots(Arrays.asList(parkingSlotRequestStandard, parkingSlotRequestEV20KW, parkingSlotRequestEV50KW));
        return parkingRequest;
    }
}