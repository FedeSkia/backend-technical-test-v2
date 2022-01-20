package com.tui.proof.mapper;

import com.tui.proof.dto.response.ClientResponse;
import com.tui.proof.model.Client;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client, Integer addressId) {
        return ClientResponse.builder()
                .clientId(client.getClientId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .telephone(client.getTelephone())
                .addressId(addressId)
                .build();
    }
}
