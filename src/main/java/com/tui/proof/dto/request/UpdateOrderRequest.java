package com.tui.proof.dto.request;

import com.tui.proof.validator.NumberOfPilotesOrderConstraint;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderRequest {

    @NotNull(message = "orderId must not be null")
    private Integer orderId;

    @NumberOfPilotesOrderConstraint
    private Integer numberOfPilotes;

    private Integer addressId;
}
