package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.ErrorDto;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PilotesControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private MockRequest mockRequest = new MockRequest();

    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PilotesOrderRepository pilotesOrderRepository;

    @After
    public void truncateTables(){
        pilotesOrderRepository.deleteAll();
        addressRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void createOrderReturns400BadRequestIfCustomerIsNotSpecified() {
        CreateOrderRequest orderWithNoClient = createOrderWithNoClient();

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", orderWithNoClient, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("client with id 1 doesn't exists", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfNumberOfPilotesIsInvalid() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(11);
        createOrderRequestRequest.setAddressId(1);
        createOrderRequestRequest.setClientId(1);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderRequestRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("Invalid pilotes number", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfClientIdIsNull() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(15);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderRequestRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        List<String> messages = errorDtoResponseEntity.getBody().getMessage();
        assertTrue(messages.contains("ClientId must not be null"));
        assertTrue(messages.contains("addressId must not be null"));
    }

    @Test
    public void createOrderReturns201WhenOrderIsPlacedAndBodyIsCorrect() {

        ResponseEntity<CreateClientRequest> clientDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/client/create", mockRequest.createValidRequest(), CreateClientRequest.class);
        CreateClientRequest createClientResponse = clientDtoResponseEntity.getBody();

        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(15);
        createOrderRequestRequest.setClientId(1);
        createOrderRequestRequest.setAddressId(1);

        ResponseEntity<PilotesOrderDtoResponse> createdOrder = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderRequestRequest, PilotesOrderDtoResponse.class);
        assertEquals(HttpStatus.CREATED, createdOrder.getStatusCode());
        PilotesOrderDtoResponse body = createdOrder.getBody();
        assertEquals(15, body.getPilotes());
        assertEquals(createClientResponse.getCity(), body.getDeliveryAddress().getCity());
        assertEquals(createClientResponse.getCountry(), body.getDeliveryAddress().getCountry());
        assertEquals(createClientResponse.getPostcode(), body.getDeliveryAddress().getPostcode());
        assertEquals(createClientResponse.getStreet(), body.getDeliveryAddress().getStreet());

        assertEquals(19.95, body.getOrderTotal(), 0);

    }

    @Test
    public void createOrderInsertOrderInOrderTable() {

        Client clientWhoPlaceOrder = clientRepository.save(Client.builder()
                .telephone("123")
                .lastName("lastName")
                .firstName("firstName")
                .build());

        Address address = addressRepository.save(Address.builder()
                .client(clientWhoPlaceOrder)
                .street("street")
                .postcode("postcode")
                .country("country")
                .city("city")
                .build());

        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(15);
        createOrderRequestRequest.setClientId(clientWhoPlaceOrder.getClientId());
        createOrderRequestRequest.setAddressId(address.getAddressId());

        ResponseEntity<PilotesOrderDtoResponse> createdOrder = restTemplate.postForEntity("http://localhost:" + port + "/order/pilotes", createOrderRequestRequest, PilotesOrderDtoResponse.class);
        int orderId = createdOrder.getBody().getOrderId();
        Optional<PilotesOrder> orderInsertedOptional = pilotesOrderRepository.findById(orderId);
        PilotesOrder orderInserted = orderInsertedOptional.get();
        assertEquals(15, orderInserted.getPilotes());
        assertEquals(address.getAddressId(), orderInserted.getDeliveryAddress().getAddressId());
        assertEquals(clientWhoPlaceOrder.getClientId(), orderInserted.getClient().getClientId());

    }

    private CreateOrderRequest createOrderWithNoClient() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(10);
        createOrderRequestRequest.setAddressId(1);
        createOrderRequestRequest.setClientId(1);
        return createOrderRequestRequest;
    }


}