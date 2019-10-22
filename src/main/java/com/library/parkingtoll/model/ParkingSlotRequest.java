package com.library.parkingtoll.model;

import com.library.parkingtoll.CarType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "All details about the Parking Slot Request.")
public class ParkingSlotRequest {
    @ApiModelProperty(
            notes = "The type of the car which can park in the slot",
            required = true,
            allowableValues = "STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW",
            example = "ELECTRIC_POWERED_20_KW"
    )
    private CarType type;

    @ApiModelProperty(
            notes = "The number of availability for car type",
            required = true,
            example = "3"
    )
    private Integer availableSpot;
}
