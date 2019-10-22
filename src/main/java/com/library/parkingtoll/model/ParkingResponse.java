package com.library.parkingtoll.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "All details about the Parking Response.")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParkingResponse extends CommonResponse {

    @ApiModelProperty(
            notes = "A unique identifier of the parking",
            example = "e0e1ce73-d8ec-43b2-81f2-d938aff7cdb1",
            required = true)

    private String parkingId;

    public ParkingResponse(String message) {
        this.message = message;
    }
}

