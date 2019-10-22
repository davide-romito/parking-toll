package com.library.parkingtoll.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "All details about the Price Response.")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceResponse extends CommonResponse {

    @ApiModelProperty(
            notes = "The date and time when the slot as been occupied by the car",
            required = true,
            example = "2019-10-21 12:30"
    )
    private String arrivalTime;

    @ApiModelProperty(
            notes = "The date and time when the slot as been left by the car",
            required = true,
            example = "2019-10-21 13:30"
    )
    private String leavingTime;

    @ApiModelProperty(
            notes = "The price for the occupied time",
            required = true,
            example = "50.0"
    )
    private Float pricing;

    public PriceResponse(String message) {
        this.message = message;
    }

}
