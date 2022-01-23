package com.tui.proof.ws.controller;

import com.tui.proof.dto.ErrorDto;
import com.tui.proof.dto.request.AuthenticationRequest;
import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.request.SearchRequest;
import com.tui.proof.dto.response.AutenticationResponse;
import com.tui.proof.dto.response.ClientResponse;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.dto.response.SearchResponse;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import com.tui.proof.ws.controller.common.MockRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientControllerTest {

    private final MockRequest mockRequest = new MockRequest();
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

    @AfterEach
    public void truncateTables() {
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
        if (size != 1) {
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
    public void searchForClientLastNameReturnsClientWithOrders() {
        Client client = storeClientInDb();
        SearchRequest request = new SearchRequest();
        request.setLastName("last");

        HttpEntity<SearchRequest> searchRequestHttpEntity = buildAuthenticatedRequest(request);
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.exchange("http://localhost:" + port + "/client/search",
                HttpMethod.POST,
                searchRequestHttpEntity,
                SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();

        assertNotNull(searchResponses);
        assertEquals(searchResponses[0].getLastName(), client.getLastName());


    }

    @Test
    public void searchForClientLastNameAndFirstNameReturnsClientWithOrders() {
        Client client = storeClientInDb();
        Address address = storeAddressInDb(client);
        SearchRequest request = new SearchRequest();
        request.setLastName("last");

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setClientId(client.getClientId());
        createOrderRequest.setAddressId(address.getAddressId());
        createOrderRequest.setNumberOfPilotes(10);
        HttpEntity<CreateOrderRequest> createOrderRequestHttpEntity = new HttpEntity<>(createOrderRequest);
        ResponseEntity<PilotesOrderDtoResponse> orderResponseEntity = restTemplate.exchange("http://localhost:" + port + "/order/create",
                HttpMethod.POST,
                createOrderRequestHttpEntity,
                PilotesOrderDtoResponse.class);

        HttpEntity<SearchRequest> searchRequestHttpEntity = buildAuthenticatedRequest(request);
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.exchange("http://localhost:" + port + "/client/search",
                HttpMethod.POST,
                searchRequestHttpEntity,
                SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();

        assertNotNull(searchResponses);
        assertEquals(searchResponses[0].getLastName(), client.getLastName());
        assertEquals(searchResponses[0].getOrders().get(0).getOrderId() ,orderResponseEntity.getBody().getOrderId());
    }

    @Test
    public void searchForClientReturnsEmptyIfItDoesntFind() {
        storeClientInDb();
        SearchRequest request = new SearchRequest();
        request.setLastName("");
        request.setName("");
        HttpEntity<SearchRequest> searchRequestHttpEntity = buildAuthenticatedRequest(request);
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.exchange("http://localhost:" + port + "/client/search",
                HttpMethod.POST,
                searchRequestHttpEntity,
                SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();
        List<SearchResponse> searchResponses1 = Arrays.asList(searchResponses);

        assertEquals(0, searchResponses1.size());

    }

    @Test
    public void searchForClientReturnsEverythingIfRequestContainsEmptyString() {
        storeClientInDb();
        SearchRequest request = new SearchRequest();
        request.setLastName("");
        HttpEntity<SearchRequest> searchRequestHttpEntity = buildAuthenticatedRequest(request);
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.exchange("http://localhost:" + port + "/client/search",
                HttpMethod.POST,
                searchRequestHttpEntity,
                SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();
        List<SearchResponse> searchResponses1 = Arrays.asList(searchResponses);

        assertEquals(clientRepository.findAll().size(), searchResponses1.size());

    }

    @Test
    public void searchForClientReturnsEverythingIfRequestContainsNull() {
        storeClientInDb();
        SearchRequest request = new SearchRequest();
        HttpEntity<SearchRequest> searchRequestHttpEntity = buildAuthenticatedRequest(request);
        ResponseEntity<SearchResponse[]> searchResponsesRespentity = restTemplate.exchange("http://localhost:" + port + "/client/search",
                HttpMethod.POST,
                searchRequestHttpEntity,
                SearchResponse[].class);
        SearchResponse[] searchResponses = searchResponsesRespentity.getBody();
        List<SearchResponse> searchResponses1 = Arrays.asList(searchResponses);

        assertEquals(clientRepository.findAll().size(), searchResponses1.size());

    }

    private Client storeClientInDb() {
        return clientRepository.save(Client.builder()
                .lastName("lastAndSome Other Char")
                .build());
    }

    private HttpEntity<SearchRequest> buildAuthenticatedRequest(SearchRequest request) {
        ResponseEntity<AutenticationResponse> autenticationResponseResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/authenticate", new AuthenticationRequest("user", "password"), AutenticationResponse.class);
        String token = autenticationResponseResponseEntity.getBody().getToken();

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<SearchRequest> searchRequestHttpEntity = new HttpEntity<>(request, headers);
        return searchRequestHttpEntity;
    }

    private Address storeAddressInDb(Client clientWhoPlaceOrder) {
        return addressRepository.save(Address.builder()
                .client(clientWhoPlaceOrder)
                .street("street")
                .postcode("postcode")
                .country("country")
                .city("city")
                .build());
    }

}