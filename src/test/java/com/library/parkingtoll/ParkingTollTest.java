package com.library.parkingtoll;

import com.library.parkingtoll.model.CarRequest;
import com.library.parkingtoll.model.CarResponse;
import com.library.parkingtoll.model.ParkingRequest;
import com.library.parkingtoll.model.ParkingResponse;
import com.library.parkingtoll.model.ParkingSlotRequest;
import com.library.parkingtoll.model.PriceRequest;
import com.library.parkingtoll.model.PriceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParkingTollTest {

    private static final String PARKING = "PARKING";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testScenario() throws Exception {
        /* Create parking */
        ParkingRequest parkingRequest = buildParkingRequest();
        ResponseEntity<ParkingResponse> parkingResponseResponseEntity = this.restTemplate.postForEntity("http://localhost:" + port + "/createParking", parkingRequest, ParkingResponse.class);
        assertTrue(parkingResponseResponseEntity.getStatusCode().is2xxSuccessful());

        /* Park cars*/
        CarRequest carRequestStandard = new CarRequest();
        carRequestStandard.setType(CarType.STANDARD);
        ResponseEntity<CarResponse> carResponseStandard = this.restTemplate.postForEntity("http://localhost:" + port + "/parking/" + PARKING + "/parkCar", carRequestStandard, CarResponse.class);
        String slotId = carResponseStandard.getBody().getSlotId();

        for (int i = 0; i < 3; i++) {
            ResponseEntity<CarResponse> otherCarResponseStandard = this.restTemplate.postForEntity("http://localhost:" + port + "/parking/" + PARKING + "/parkCar", carRequestStandard, CarResponse.class);
            assertTrue(otherCarResponseStandard.getStatusCode().is2xxSuccessful());
        }

        CarRequest carRequestEV50kw = new CarRequest();
        carRequestEV50kw.setType(CarType.ELECTRIC_POWERED_50_KW);
        ResponseEntity<CarResponse> carResponseEV50kw = this.restTemplate.postForEntity("http://localhost:" + port + "/parking/" + PARKING + "/parkCar", carRequestEV50kw, CarResponse.class);
        assertTrue(carResponseEV50kw.getStatusCode().is2xxSuccessful());

        /* Park car with no slot available */
        ResponseEntity<CarResponse> carResponseEV50kwSecondTime = this.restTemplate.postForEntity("http://localhost:" + port + "/parking/" + PARKING + "/parkCar", carRequestEV50kw, CarResponse.class);
        assertTrue(carResponseEV50kwSecondTime.getStatusCode().is4xxClientError());
        assertEquals("Parking slot not found", carResponseEV50kwSecondTime.getBody().getMessage());

        /* Trying to create a parking with the same id */
        ResponseEntity<ParkingResponse> parkingResponseSecondTime = this.restTemplate.postForEntity("http://localhost:" + port + "/createParking", parkingRequest, ParkingResponse.class);
        assertTrue(parkingResponseSecondTime.getStatusCode().is4xxClientError());
        assertEquals("Parking [PARKING] already exist!", parkingResponseSecondTime.getBody().getMessage());

        /* Take a car from a fake parking */
        ResponseEntity<PriceResponse> priceResponseFakeParking = this.restTemplate.getForEntity("http://localhost:" + port + "/parking/FAKE_PARKING/takeCar/" + slotId, PriceResponse.class);
        assertTrue(priceResponseFakeParking.getStatusCode().is4xxClientError());

        /* Take a car from a not existing slot */
        ResponseEntity<PriceResponse> priceResponseNoSlot = this.restTemplate.getForEntity("http://localhost:" + port + "/parking/" + PARKING + "/takeCar/5000", PriceResponse.class);
        assertTrue(priceResponseNoSlot.getStatusCode().is4xxClientError());

        TimeUnit.MINUTES.sleep(1);//To trigger the hour rate
        /* Take first car entered */
        ResponseEntity<PriceResponse> priceResponse = this.restTemplate.getForEntity("http://localhost:" + port + "/parking/" + PARKING + "/takeCar/" + slotId, PriceResponse.class);
        assertTrue(priceResponse.getStatusCode().is2xxSuccessful());
        assertEquals(5, priceResponse.getBody().getPricing());

    }


    private ParkingRequest buildParkingRequest() {
        ParkingRequest parkingRequest = new ParkingRequest();
        parkingRequest.setParkingName(PARKING);
        parkingRequest.setPricingPolicy("HOURLY");
        PriceRequest priceRequest = new PriceRequest();
        priceRequest.setHourRate(5F);
        parkingRequest.setPrice(priceRequest);
        ParkingSlotRequest parkingSlotRequestStandard = new ParkingSlotRequest();
        parkingSlotRequestStandard.setType(CarType.STANDARD);
        parkingSlotRequestStandard.setAvailableSpot(5);
        ParkingSlotRequest parkingSlotRequestEV20KW = new ParkingSlotRequest();
        parkingSlotRequestEV20KW.setType(CarType.ELECTRIC_POWERED_20_KW);
        parkingSlotRequestEV20KW.setAvailableSpot(2);
        ParkingSlotRequest parkingSlotRequestEV50KW = new ParkingSlotRequest();
        parkingSlotRequestEV50KW.setType(CarType.ELECTRIC_POWERED_50_KW);
        parkingSlotRequestEV50KW.setAvailableSpot(1);
        parkingRequest.setParkingSlots(Arrays.asList(parkingSlotRequestStandard, parkingSlotRequestEV20KW, parkingSlotRequestEV50KW));
        return parkingRequest;
    }
}
