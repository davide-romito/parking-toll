package com.library.parkingtoll.model;

import com.library.parkingtoll.CarType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "All details about the Car Request.")
@Data
public class CarRequest {
    @ApiModelProperty(
            notes = "The type of the car",
            required = true,
            allowableValues = "STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW",
            example = "ELECTRIC_POWERED_20_KW"
    )
    private CarType type;
}

