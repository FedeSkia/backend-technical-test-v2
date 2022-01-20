package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.dto.ErrorDto;
import com.tui.proof.dto.request.SearchRequest;
import com.tui.proof.dto.response.ClientResponse;
import com.tui.proof.dto.response.SearchResponse;
import com.tui.proof.model.Address;
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

        ResponseEntity<ClientResponse> clientDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/client/create", request, ClientResponse.class);
        assertEquals(HttpStatus.CREATED, clientDtoResponseEntity.getStatusCode());
        ClientResponse responseBody = clientDtoResponseEntity.getBody();
        assertEquals(request.getName(), responseBody.getFirstName());
        assertEquals(request.getLastName(), responseBody.getLastName());
        assertEquals(request.getTelephone(), responseBody.getTelephone());

        List<Address> all = addressRepository.findAll();
        assertEquals((Integer) all.get(0).getAddressId(), responseBody.getAddressId());
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

    @Test
    public void searchForClientLastNameReturnsClientWithOrders(){
        Client client = storeClientInDb();
        SearchRequest request = new SearchRequest();
        request.setLastName("last");
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.postForEntity("http://localhost:" + port + "/client/search", request, SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();

        assertNotNull(searchResponses);
        assertEquals(searchResponses[0].getLastName(), client.getLastName());


    }

    @Test
    public void searchForClientLastNameAndFirstNameReturnsClientWithOrders(){
        Client client = storeClientInDb();
        SearchRequest request = new SearchRequest();
        request.setLastName("last");
        request.setName("name very weird");
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.postForEntity("http://localhost:" + port + "/client/search", request, SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();

        assertNotNull(searchResponses);
        assertEquals(searchResponses[0].getLastName(), client.getLastName());
    }

    private Client storeClientInDb() {
        return clientRepository.save(Client.builder()
                .lastName("lastAndSome Other Char")
                .build());
    }

}