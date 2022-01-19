package com.tui.proof.dto;

import com.tui.proof.validator.NumberOfPilotesOrderConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderDto {

    @NotNull(message = "ClientId must not be null")
    private Integer clientId;

    @NumberOfPilotesOrderConstraint
    private Integer numberOfPilotes;

    private Integer addressId;

}
