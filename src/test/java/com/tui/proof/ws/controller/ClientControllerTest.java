package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.dto.ErrorDto;
import com.tui.proof.model.Client;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import com.tui.proof.ws.controller.common.MockRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PilotesOrderRepository pilotesOrderRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private MockRequest mockRequest = new MockRequest();

    @After
    public void truncateTables(){
        pilotesOrderRepository.deleteAll();
        addressRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void createCustomerReturnsExpectedBody() {
        CreateClientRequest request = mockRequest.createValidRequest();

        ResponseEntity<CreateClientRequest> clientDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/client/create", request, CreateClientRequest.class);
        assertEquals(HttpStatus.CREATED, clientDtoResponseEntity.getStatusCode());
        CreateClientRequest responseBody = clientDtoResponseEntity.getBody();
        assertEquals(request.getName(), responseBody.getName());
        assertEquals(request.getLastName(), responseBody.getLastName());
        assertEquals(request.getTelephone(), responseBody.getTelephone());
    }


    @Test
    public void createCustomerInsertClientInDb() {
        CreateClientRequest validRequest = mockRequest.createValidRequest();
        restTemplate.postForEntity("http://localhost:" + port + "/client/create", validRequest, CreateClientRequest.class);
        List<Client> client = clientRepository.findAll();
        int size = client.size();
        if( size !=  1){
            fail("expect the test databsae to be clean. Clients must be 1");
        }

        Client clienteCreated = client.get(0);

        assertEquals(validRequest.getName(), clienteCreated.getFirstName());
        assertEquals(validRequest.getLastName(), clienteCreated.getLastName());
        assertEquals(validRequest.getTelephone(), clienteCreated.getTelephone());
        addressRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void createCustomerValidation() {
        CreateClientRequest request = new CreateClientRequest();
        request.setLastName("");
        request.setTelephone("32199999999999999999999321321");

        ResponseEntity<ErrorDto> clientDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/client/create", request, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, clientDtoResponseEntity.getStatusCode());
        ErrorDto body = clientDtoResponseEntity.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, body.getStatus());
        List<String> messages = body.getMessage();
        assertTrue(messages.contains("name is mandatory"));
        assertTrue(messages.contains("lastName is mandatory"));
        assertTrue(messages.contains("street must not be empty"));
        assertTrue(messages.contains("postcode must not be empty"));
        assertTrue(messages.contains("city must not be empty"));
        assertTrue(messages.contains("country must not be empty"));

    }

}