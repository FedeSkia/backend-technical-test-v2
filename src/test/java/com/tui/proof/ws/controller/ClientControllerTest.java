package com.tui.proof.ws.controller;

import com.tui.proof.dto.ClientDto;
import com.tui.proof.dto.ErrorDto;
import com.tui.proof.model.Client;
import com.tui.proof.repository.ClientRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    ClientRepository clientRepository;

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
        clientRepository.deleteById(1);
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
        assertTrue(messages.contains("Invalid phone number"));

    }


    private ClientDto createValidRequest() {
        ClientDto request = new ClientDto();
        request.setName("name");
        request.setLastName("lastName");
        request.setTelephone("321321321");
        return request;
    }
}