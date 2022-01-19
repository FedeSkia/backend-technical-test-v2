package com.tui.proof.ws.controller;

import com.tui.proof.dto.CreateOrderDto;
import com.tui.proof.dto.ErrorDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PilotesControllerTest {

    @Autowired
    private PilotesController pilotesController;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createOrderReturns400BadRequestIfCustomerIsNotSpecified() {
        CreateOrderDto orderWithNoClient = createOrderWithNoClient();

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", orderWithNoClient, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("client with id 1 doesn't exists", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfNumberOfPilotesIsInvalid() {
        CreateOrderDto createOrderDtoRequest = new CreateOrderDto();
        createOrderDtoRequest.setNumberOfPilotes(11);
        createOrderDtoRequest.setAddressId(1);
        createOrderDtoRequest.setClientId(1);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderDtoRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("Invalid pilotes number", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfClientIdIsNull() {
        CreateOrderDto createOrderDtoRequest = new CreateOrderDto();
        createOrderDtoRequest.setNumberOfPilotes(15);
        createOrderDtoRequest.setAddressId(1);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderDtoRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("ClientId must not be null", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    private CreateOrderDto createOrderWithNoClient() {
        CreateOrderDto createOrderDtoRequest = new CreateOrderDto();
        createOrderDtoRequest.setNumberOfPilotes(10);
        createOrderDtoRequest.setAddressId(1);
        createOrderDtoRequest.setClientId(1);
        return createOrderDtoRequest;
    }


}