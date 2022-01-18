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

}
