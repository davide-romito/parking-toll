package com.library.parkingtoll.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.parkingtoll.CarType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiModel(description = "All details about the Parking Slot Response.")
@Data
@RequiredArgsConstructor
public class ParkingSlotResponse {
    @ApiModelProperty(
            notes = "The slotId in the parking",
            required = true,
            example = "15"
    )
    @NonNull
    private String slotId;

    @ApiModelProperty(
            notes = "The type of the car which can use this parking slot",
            required = true,
            allowableValues = "STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW",
            example = "ELECTRIC_POWERED_20_KW"
    )
    @NonNull
    private CarType carType;

    @ApiModelProperty(
            notes = "The available status of the parking slot",
            required = true,
            example = "true"
    )
    @NonNull
    private boolean available;

    @ApiModelProperty(
            notes = "The date and time when the slot as been occupied by the car",
            example = "2019-10-21 12:30"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String arrivalTime;

}
