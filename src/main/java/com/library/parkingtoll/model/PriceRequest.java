package com.library.parkingtoll.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@ApiModel(description = "All details about the Price Request.")
@Data
public class PriceRequest {

    @ApiModelProperty(
            notes = "Amount for hour rate",
            example = "5.0")
    private Float hourRate;

    @ApiModelProperty(
            notes = "Amount for the fix rate",
            example = "10.0")
    private Float fixRate;

    public Map<String, Float> toMap() {
        Map<String, Float> prices = new HashMap<>();
        prices.put("HOUR_PRICE", this.hourRate);
        prices.put("FIX_PRICE", this.fixRate);
        return prices;
    }
}
