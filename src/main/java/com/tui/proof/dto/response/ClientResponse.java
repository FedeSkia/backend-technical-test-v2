package com.tui.proof.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientResponse {

    private Integer clientId;
    private String firstName;
    private String lastName;
    private String telephone;
    private Integer addressId;

}
