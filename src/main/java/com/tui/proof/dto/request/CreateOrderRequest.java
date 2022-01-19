package com.tui.proof.dto.request;

import com.tui.proof.validator.NumberOfPilotesOrderConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateOrderRequest {

    @NotNull(message = "ClientId must not be null")
    private Integer clientId;

    @NumberOfPilotesOrderConstraint
    private Integer numberOfPilotes;

    @NotNull(message = "addressId must not be null")
    private Integer addressId;

}
