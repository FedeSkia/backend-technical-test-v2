package com.tui.proof.ws.controller.common;

import com.tui.proof.dto.request.CreateClientRequest;

public class MockRequest {

    public CreateClientRequest createValidRequest(){
        CreateClientRequest request = new CreateClientRequest();
        request.setName("name");
        request.setLastName("lastName");
        request.setTelephone("321321321");
        request.setStreet("Via 123");
        request.setCity("Lecce");
        request.setPostcode("73100");
        request.setCountry("Italy");
        return request;
    }

}
