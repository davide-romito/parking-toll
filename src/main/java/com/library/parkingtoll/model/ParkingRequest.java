package com.library.parkingtoll.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(description = "All details about the Parking Request.")
@Data
public class ParkingRequest {

    @ApiModelProperty(
            notes = "The identification of the parking",
            example = "Parking XYZ"
    )
    private String parkingName;

    @ApiModelProperty(
            notes = "The pricing policy of the parking",
            required = true,
            allowableValues = "HOURLY, FIX_PLUS_HOURLY",
            example = "FIX_PLUS_HOURLY"
    )
    private String pricingPolicy;

    @ApiModelProperty(
            notes = "The price policy for the parking",
            required = true
    )
    private PriceRequest price;

    @ApiModelProperty(
            notes = "The list of parking slot by type",
            required = true
    )
    private List<ParkingSlotRequest> parkingSlots;
}