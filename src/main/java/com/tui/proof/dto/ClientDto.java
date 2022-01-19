package com.tui.proof.dto;

import com.tui.proof.validator.TelephoneConstraint;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class ClientDto {

    @NotEmpty(message = "name is mandatory")
    private String name;
    @NotEmpty(message = "lastName is mandatory")
    private String lastName;
    @TelephoneConstraint
    private String telephone;

    @NotEmpty(message = "street must not be empty")
    private String street;
    @NotEmpty(message = "postcode must not be empty")
    private String postcode;
    @NotEmpty(message = "city must not be empty")
    private String city;
    @NotEmpty(message = "country must not be empty")
    private String country;

}
