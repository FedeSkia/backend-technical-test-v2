package com.tui.proof.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tui.proof.model.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PilotesOrderDtoResponse {

    private int orderId;
    private AddressResponse deliveryAddress;
    private ClientResponse client;
    private int pilotes;
    private double orderTotal;

}
