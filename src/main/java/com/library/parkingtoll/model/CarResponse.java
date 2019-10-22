package com.library.parkingtoll.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.library.parkingtoll.CarType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "All details about the Car Response.")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarResponse extends CommonResponse {
    @ApiModelProperty(
            notes = "The slotId in the parking",
            required = true,
            example = "15"
    )
    private String slotId;

    @ApiModelProperty(
            notes = "The type of the car",
            required = true,
            allowableValues = "STANDARD, ELECTRIC_POWERED_20_KW, ELECTRIC_POWERED_50_KW",
            example = "ELECTRIC_POWERED_20_KW"
    )
    private CarType type;

    @ApiModelProperty(
            notes = "The date and time when the slot as been occupied by the car",
            required = true,
            example = "2019-10-21 12:30"
    )
    private String occupationTime;

    public CarResponse(String message) {
        this.message = message;
    }
}

