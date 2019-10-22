package com.library.parkingtoll;

import com.library.parkingtoll.model.CarRequest;
import com.library.parkingtoll.model.CarResponse;
import com.library.parkingtoll.model.ParkingRequest;
import com.library.parkingtoll.model.ParkingResponse;
import com.library.parkingtoll.model.PriceResponse;
import com.library.parkingtoll.service.ParkingService;
import com.library.parkingtoll.service.parking.exception.ParkingAlreadyExistException;
import com.library.parkingtoll.service.parking.exception.ParkingNotFoundException;
import com.library.parkingtoll.service.parking.exception.ParkingSlotException;
import com.library.parkingtoll.service.pricing.exception.PricingPolicyException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(
        value = "Parking Toll API",
        tags = {"Parking-Toll "}
)
@Log4j2
@RestController
public class Controller {
    private final ParkingService parkingService;

    @Autowired
    public Controller(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @ApiOperation(value = "Create a parking")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            response = ParkingResponse.class,
                            message = "OK - We sent back the ParkingResponse"
                    ),
                    @ApiResponse(
                            code = 400,
                            response = ParkingResponse.class,
                            message = "Error in the request:\n"
                                    + " - if not value in the price Request : Not valued prices for Pricing Policy implemented\n"
                                    + " - if pricing type not implemented yet: Pricing Policy not implemented yet"

                    ),
                    @ApiResponse(
                            code = 409,
                            response = ParkingResponse.class,
                            message = "Error: Parking [parking] already exist!"
                    )
            }
    )
    @PostMapping(value = "createParking", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParkingResponse> createParking(@RequestBody ParkingRequest parkingRequest) {
        ParkingResponse response;
        try {
            response = parkingService.createParking(parkingRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (PricingPolicyException e) {
            log.warn(e.getMessage());
            response = new ParkingResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (ParkingAlreadyExistException e) {
            log.error(e.getMessage());
            response = new ParkingResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Park a car")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            response = CarResponse.class,
                            message = "OK - We sent back the CarResponse."
                    ),
                    @ApiResponse(
                            code = 404,
                            response = CarResponse.class,
                            message = "Error: Parking not found - issue on our side"
                    ),
                    @ApiResponse(
                            code = 400,
                            response = CarResponse.class,
                            message = "Error: Parking slot not found"
                    )
            }
    )
    @PostMapping(value = "parking/{parkingId}/parkCar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CarResponse> parkCar(
            @ApiParam(value = "Id of the chosen parking", required = true) @PathVariable(name = "parkingId") String parkingId,
            @RequestBody CarRequest carRequest) {
        CarResponse response;
        try {
            response = parkingService.parkCar(parkingId, carRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParkingNotFoundException e) {
            log.error(e.getMessage());
            response = new CarResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ParkingSlotException e) {
            log.error(e.getMessage());
            response = new CarResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    @ApiOperation(value = "Take the car parked in the parking slot", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            response = PriceResponse.class,
                            message = "OK - We sent back the PriceResponse."
                    ),
                    @ApiResponse(
                            code = 404,
                            response = PriceResponse.class,
                            message = "Error: Parking not found - issue on our side"
                    ),
                    @ApiResponse(
                            code = 400,
                            response = PriceResponse.class,
                            message = "Error in the request:\n"
                                    + " - if parkSlot doesn't exist : Parking slot not found\n"
                                    + " - if parkSlot wasn't occupied: Parking slot not occupied"

                    )
            }
    )
    @GetMapping(value = "parking/{parkingId}/takeCar/{parkingSlot}")
    public ResponseEntity<PriceResponse> takeCar(
            @ApiParam(value = "Id of the chosen parking", required = true) @PathVariable(name = "parkingId") String parkingId,
            @ApiParam(value = "Parking slot where the car has been parked", required = true) @PathVariable String parkingSlot) {
        PriceResponse response;
        try {
            response = parkingService.takeCar(parkingId, parkingSlot);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ParkingNotFoundException e) {
            log.error(e.getMessage());
            response = new PriceResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (ParkingSlotException e) {
            log.error(e.getMessage());
            response = new PriceResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
