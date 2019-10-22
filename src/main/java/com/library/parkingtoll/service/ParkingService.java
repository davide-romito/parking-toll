package com.library.parkingtoll.service;

import com.library.parkingtoll.CarType;
import com.library.parkingtoll.model.CarRequest;
import com.library.parkingtoll.model.CarResponse;
import com.library.parkingtoll.model.ParkingRequest;
import com.library.parkingtoll.model.ParkingResponse;
import com.library.parkingtoll.model.ParkingSlotResponse;
import com.library.parkingtoll.model.PriceResponse;
import com.library.parkingtoll.service.parking.Parking;
import com.library.parkingtoll.service.parking.ParkingSlot;
import com.library.parkingtoll.service.parking.exception.ParkingAlreadyExistException;
import com.library.parkingtoll.service.parking.exception.ParkingNotFoundException;
import com.library.parkingtoll.service.parking.exception.ParkingSlotException;
import com.library.parkingtoll.service.pricing.PricingPolicy;
import com.library.parkingtoll.service.pricing.PricingPolicyFactory;
import com.library.parkingtoll.service.pricing.exception.PricingPolicyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ParkingService {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Map<String, Parking> map;

    public ParkingService() {
        map = new HashMap<>();
    }

    /**
     * Method to create a parking using the data from <code>parkingRequest</code>
     *
     * @return Parking response with the id which identified the Parking
     */
    public ParkingResponse createParking(ParkingRequest parkingRequest) throws PricingPolicyException, ParkingAlreadyExistException {
        if (!StringUtils.isEmpty(parkingRequest.getParkingName()) && map.containsKey(parkingRequest.getParkingName())) {
            throw new ParkingAlreadyExistException("Parking [" + parkingRequest.getParkingName() + "] already exist!");
        }
        PricingPolicy pricingPolicy = PricingPolicyFactory.createPricingFactory(parkingRequest.getPricingPolicy(), parkingRequest.getPrice().toMap());

        Parking parking = new Parking(parkingRequest.getParkingName(), pricingPolicy);
        parkingRequest.getParkingSlots().forEach(parkingSlot -> parking.addParkingSlotByType(parkingSlot.getType(), parkingSlot.getAvailableSpot()));
        map.put(parking.getParkingId(), parking);

        return buildParkingResponse(parking);
    }

    /**
     * Method to retrieve the parking, and the current status of the parking slots
     * @return Parking response of a given parkingId
     * @throws ParkingNotFoundException in case the parking identified with parkingId doesn't exist
     */
    public ParkingResponse getParking(String parkingId) throws ParkingNotFoundException {
        parkingValidation(parkingId);
        Parking parking = map.get(parkingId);
        return buildParkingResponse(parking);
    }

    private ParkingResponse buildParkingResponse(Parking parking) {
        ParkingResponse parkingResponse = new ParkingResponse();
        parkingResponse.setParkingId(parking.getParkingId());
        List<ParkingSlotResponse> list = new ArrayList<>();
        for (Map.Entry<CarType, Set<ParkingSlot>> carTypeSetEntry : parking.getSlotsByType().entrySet()) {
            carTypeSetEntry.getValue().stream()
                    .sorted(Comparator.comparingInt(o -> Integer.parseInt(o.getSlot())))
                    .map(parkingSlot -> {
                        ParkingSlotResponse parkingSlotResponse = new ParkingSlotResponse(parkingSlot.getSlot(), carTypeSetEntry.getKey(), parkingSlot.isAvailable());
                        if (parkingSlot.getTakenTime() != null){
                            parkingSlotResponse.setArrivalTime(parkingSlot.getTakenTime().format(DATETIME_FORMATTER));
                        }
                        return parkingSlotResponse;
                    })
                    .forEach(list::add);
        }
        parkingResponse.setParkingSlots(list);
        return parkingResponse;
    }

    /**
     * Method to park a car in the appropriate slot of the parking identified with <code>parkingId</code>.
     * It will check if any slot of the same type of the car is available,
     * in case it will reserve the slot and it will return the slot id
     *
     * @return slot id
     * @throws ParkingSlotException     in case the parking slot identified with parkingSlotId doesn't exist
     * @throws ParkingNotFoundException in case the parking identified with parkingId doesn't exist
     */
    public CarResponse parkCar(String parkingId, CarRequest carRequest) throws ParkingSlotException, ParkingNotFoundException {
        parkingValidation(parkingId);
        Parking parking = map.get(parkingId);

        Set<ParkingSlot> parkingSlots = parking.getSlotsByType(carRequest.getType());

        ParkingSlot parkingSlot = parkingSlots.stream().filter(ParkingSlot::isAvailable).findFirst()
                .orElseThrow(() -> new ParkingSlotException("Parking slot not found"));
        LocalDateTime takenTime = LocalDateTime.now();
        parkingSlot.takeSlot(takenTime);

        CarResponse carResponse = new CarResponse();
        carResponse.setOccupationTime(takenTime.format(DATETIME_FORMATTER));
        carResponse.setSlotId(parkingSlot.getSlot());
        carResponse.setType(carRequest.getType());
        return carResponse;
    }

    /**
     * Method to take a car from a slot of the parking identified with <code>parkingId</code>.
     * It will release the slot and calculate the price of the parking
     *
     * @return price amount
     * @throws ParkingSlotException     in case the parking slot identified with parkingSlotId doesn't exist or
     *                                  if the slot is available
     * @throws ParkingNotFoundException in case the parking identified with parkingId doesn't exist
     */
    public PriceResponse takeCar(String parkingId, String parkingSlotId) throws ParkingSlotException, ParkingNotFoundException {
        parkingValidation(parkingId);
        Parking parking = map.get(parkingId);

        ParkingSlot slot = parking.getSlotsByType().values().stream()
                .flatMap(Collection::stream)
                .filter(ps -> ps.getSlot().equals(parkingSlotId))
                .findAny().orElseThrow(() -> new ParkingSlotException("Parking slot not found"));

        if (slot.isAvailable()) {
            throw new ParkingSlotException("Parking slot not occupied");
        }
        LocalDateTime leaveDateTime = LocalDateTime.now();
        LocalDateTime enterDateTime = slot.leaveSlot();

        float price = parking.getPricingPolicy().invoice(enterDateTime, leaveDateTime);
        PriceResponse priceResponse = new PriceResponse();
        priceResponse.setArrivalTime(enterDateTime.format(DATETIME_FORMATTER));
        priceResponse.setLeavingTime(leaveDateTime.format(DATETIME_FORMATTER));
        priceResponse.setPricing(price);
        return priceResponse;
    }

    /**
     * Check if the parking identified with parkingId exist
     */
    private void parkingValidation(String parkingId) throws ParkingNotFoundException {
        if (!map.containsKey(parkingId)) {
            throw new ParkingNotFoundException("Parking [" + parkingId + "] not found");
        }
    }
}
