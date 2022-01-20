package com.tui.proof.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddressResponse {

    private Integer addressId;
    private String street;
    private String postcode;
    private String city;
    private String country;

}
