package com.tui.proof.ws.controller;

import com.tui.proof.dto.ClientDto;
import com.tui.proof.dto.ErrorDto;
import com.tui.proof.model.Client;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AddressRepository addressRepository;
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createCustomerReturnsExpectedBody() {
        ClientDto request = createValidRequest();

        ResponseEntity<ClientDto> clientDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/client/create", request, ClientDto.class);
        assertEquals(HttpStatus.CREATED, clientDtoResponseEntity.getStatusCode());
        ClientDto responseBody = clientDtoResponseEntity.getBody();
        assertEquals(request.getName(), responseBody.getName());
        assertEquals(request.getLastName(), responseBody.getLastName());
        assertEquals(request.getTelephone(), responseBody.getTelephone());
    }


    @Test
    public void createCustomerInsertClientInDb() {
        ClientDto validRequest = createValidRequest();
        restTemplate.postForEntity("http://localhost:" + port + "/client/create", validRequest, ClientDto.class);
        Client client = clientRepository.findClientByClientId(1);
        assertEquals(validRequest.getName(), client.getFirstName());
        assertEquals(validRequest.getLastName(), client.getLastName());
        assertEquals(validRequest.getTelephone(), client.getTelephone());
        addressRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void createCustomerValidation() {
        ClientDto request = new ClientDto();
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


    private ClientDto createValidRequest() {
        ClientDto request = new ClientDto();
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