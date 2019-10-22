package com.library.parkingtoll;

import com.library.parkingtoll.model.CarRequest;
import com.library.parkingtoll.model.CarResponse;
import com.library.parkingtoll.model.ParkingRequest;
import com.library.parkingtoll.model.ParkingResponse;
import com.library.parkingtoll.model.PriceResponse;
import com.library.parkingtoll.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {
    private static final String PARKING_REQUEST = "{\"parkingName\": \"parking\","
            + "\"pricingPolicy\":\"FIX_PLUS_HOURLY\","
            + "\"price\": {\"hourRate\" : 5.0, \"fixRate\" : 10.0},"
            + " \"parkingSlots\" : [{\"type\" : \"STANDARD\",\"availableSpot\" : 5}, {\"type\" : \"ELECTRIC_POWERED_20_KW\",\"availableSpot\" : 5}, {\"type\" : \"ELECTRIC_POWERED_50_KW\",\"availableSpot\" : 5}]}";

    private static final String CAR_REQUEST = "{\"type\" : \"STANDARD\"}";

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private Controller controller;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreateParking() throws Exception {
        Mockito.when(parkingService.createParking(any(ParkingRequest.class))).thenReturn(new ParkingResponse());
        this.mockMvc.perform(post("/createParking").contentType(MediaType.APPLICATION_JSON_VALUE).content(PARKING_REQUEST)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testGetParking() throws Exception {
        Mockito.when(parkingService.getParking(anyString())).thenReturn(new ParkingResponse());
        this.mockMvc.perform(get("/parking/parkingId").content(CAR_REQUEST)).andDo(print()).andExpect(status().isOk());
    }


    @Test
    void testParkCar() throws Exception {
        Mockito.when(parkingService.parkCar(anyString(), any(CarRequest.class))).thenReturn(new CarResponse());
        this.mockMvc.perform(post("/parking/parkingId/parkCar").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE).content(CAR_REQUEST)).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void testTakeCar() throws Exception {
        Mockito.when(parkingService.takeCar(anyString(), anyString())).thenReturn(new PriceResponse());
        this.mockMvc.perform(get("/parking/parkingId/takeCar/5")).andDo(print()).andExpect(status().isOk());

    }
}