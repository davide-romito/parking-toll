package com.library.parkingtoll.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel("CommonResponse")
@Getter
public abstract class CommonResponse {
    @ApiModelProperty(
            notes = "Message in the response to return error.")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String message;

}
