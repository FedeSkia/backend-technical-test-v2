package com.tui.proof.dto.response;

import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PilotesOrderDtoResponse {

    private int orderId;
    private Address deliveryAddress;
    private Client client;
    private int pilotes;
    private double orderTotal;

}
