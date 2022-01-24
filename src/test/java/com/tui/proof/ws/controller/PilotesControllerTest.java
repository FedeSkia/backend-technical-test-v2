package com.tui.proof.ws.controller;

import com.tui.proof.dto.ErrorDto;
import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.request.UpdateOrderRequest;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import com.tui.proof.ws.controller.common.MockRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PilotesControllerTest {

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

    @Value("${price}")
    private Double price;

    @Value("${numberOfPilotes}")
    private List<Integer> pilotesNumber;

    @AfterEach
    public void truncateTables() {
        pilotesOrderRepository.deleteAll();
        addressRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void createOrderReturns400BadRequestIfCustomerIsNotSpecified() {
        CreateOrderRequest orderWithNoClient = createOrderWithNoClient();

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/create", orderWithNoClient, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("client with id 1 doesn't exists", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfNumberOfPilotesIsInvalid() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(11);
        createOrderRequestRequest.setAddressId(1);
        createOrderRequestRequest.setClientId(1);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/create", createOrderRequestRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        assertEquals("Invalid pilotes number", errorDtoResponseEntity.getBody().getMessage().get(0));
    }

    @Test
    public void createOrderReturns400BadRequestIfClientIdIsNull() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(15);

        ResponseEntity<ErrorDto> errorDtoResponseEntity = restTemplate.postForEntity("http://localhost:" + port + "/order/create", createOrderRequestRequest, ErrorDto.class);
        assertEquals(HttpStatus.BAD_REQUEST, errorDtoResponseEntity.getStatusCode());
        List<String> messages = errorDtoResponseEntity.getBody().getMessage();
        assertTrue(messages.contains("ClientId must not be null"));
        assertTrue(messages.contains("addressId must not be null"));
    }

    @Test
    public void createOrderReturns201WhenOrderIsPlacedAndBodyIsCorrect() {

        Client clientWhoPlaceOrder = storeClientInDb();

        Address address = storeAddressInDb(clientWhoPlaceOrder);

        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(pilotesNumber.get(0));
        createOrderRequestRequest.setClientId(clientWhoPlaceOrder.getClientId());
        createOrderRequestRequest.setAddressId(address.getAddressId());

        ResponseEntity<PilotesOrderDtoResponse> createdOrder = restTemplate.postForEntity("http://localhost:" + port + "/order/create", createOrderRequestRequest, PilotesOrderDtoResponse.class);
        assertEquals(HttpStatus.CREATED, createdOrder.getStatusCode());
        PilotesOrderDtoResponse body = createdOrder.getBody();
        assertEquals(pilotesNumber.get(0), (Integer) body.getPilotes());
        assertEquals(address.getCity(), body.getDeliveryAddress().getCity());
        assertEquals(address.getCountry(), body.getDeliveryAddress().getCountry());
        assertEquals(address.getPostcode(), body.getDeliveryAddress().getPostcode());
        assertEquals(address.getStreet(), body.getDeliveryAddress().getStreet());

        BigDecimal expectedPrice = BigDecimal.valueOf(createOrderRequestRequest.getNumberOfPilotes() * price)
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedPrice.doubleValue(), body.getOrderTotal(), 0);

    }

    @Test
    public void createOrderStoresExpectedOrderInPilotesOrderTable() {

        Client clientWhoPlaceOrder = storeClientInDb();
        Address address = storeAddressInDb(clientWhoPlaceOrder);

        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(15);
        createOrderRequestRequest.setClientId(clientWhoPlaceOrder.getClientId());
        createOrderRequestRequest.setAddressId(address.getAddressId());

        ResponseEntity<PilotesOrderDtoResponse> createdOrder = restTemplate.postForEntity("http://localhost:" + port + "/order/create", createOrderRequestRequest, PilotesOrderDtoResponse.class);

        int orderId = createdOrder.getBody().getOrderId();
        PilotesOrder pilotesOrder = pilotesOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("order must be present"));
        assertEquals(pilotesOrder.getPilotes(), createdOrder.getBody().getPilotes());
        assertNotNull(pilotesOrder.getPlacedOn());
        assertEquals(pilotesOrder.getClient().getClientId(), clientWhoPlaceOrder.getClientId());
    }

    @Test
    public void createOrderInsertOrderInOrderTable() {

        Client clientWhoPlaceOrder = storeClientInDb();
        Address address = storeAddressInDb(clientWhoPlaceOrder);

        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(pilotesNumber.get(0));
        createOrderRequestRequest.setClientId(clientWhoPlaceOrder.getClientId());
        createOrderRequestRequest.setAddressId(address.getAddressId());

        ResponseEntity<PilotesOrderDtoResponse> createdOrder = restTemplate.postForEntity("http://localhost:" + port + "/order/create", createOrderRequestRequest, PilotesOrderDtoResponse.class);
        int orderId = createdOrder.getBody().getOrderId();
        Optional<PilotesOrder> orderInsertedOptional = pilotesOrderRepository.findById(orderId);
        PilotesOrder orderInserted = orderInsertedOptional.get();
        assertEquals(createOrderRequestRequest.getNumberOfPilotes(), (Integer) orderInserted.getPilotes());
        assertEquals(address.getAddressId(), orderInserted.getDeliveryAddress().getAddressId());
        assertEquals(clientWhoPlaceOrder.getClientId(), orderInserted.getClient().getClientId());

    }

    @Test
    public void updateOrderPerformsUnUpdateAndReturnsUpdatedOrder() {
        Client clientWhoPlaceOrder = storeClientInDb();
        Address address = storeAddressInDb(clientWhoPlaceOrder);
        PilotesOrder pilotesOrder = storeOrderInDb(clientWhoPlaceOrder, address, LocalDateTime.now());

        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
        updateOrderRequest.setOrderId(pilotesOrder.getOrderId());
        updateOrderRequest.setNumberOfPilotes(pilotesNumber.get(0));
        updateOrderRequest.setAddressId(address.getAddressId());

        HttpEntity<UpdateOrderRequest> requestHttpEntity = new HttpEntity<>(updateOrderRequest);

        ResponseEntity<PilotesOrderDtoResponse> updateResponse = restTemplate.exchange("http://localhost:" + port + "/order/update",
                HttpMethod.PUT,
                requestHttpEntity,
                PilotesOrderDtoResponse.class);

        PilotesOrderDtoResponse updateResponseBody = updateResponse.getBody();
        assertEquals((Integer) clientWhoPlaceOrder.getClientId(), updateResponseBody.getClient().getClientId());
        assertEquals((Integer)address.getAddressId(), updateResponseBody.getDeliveryAddress().getAddressId());
        assertEquals((int) updateOrderRequest.getNumberOfPilotes(), updateResponseBody.getPilotes());

        BigDecimal expectedPrice = BigDecimal.valueOf(updateOrderRequest.getNumberOfPilotes() * price)
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedPrice.doubleValue(), updateResponseBody.getOrderTotal(), 0);

    }

    @Test
    public void updateOrderCantBePlacedAfter5mins() {
        Client clientWhoPlaceOrder = storeClientInDb();
        Address address = storeAddressInDb(clientWhoPlaceOrder);
        PilotesOrder pilotesOrder = storeOrderInDb(clientWhoPlaceOrder, address, LocalDateTime.now().minusMinutes(5).minus(Duration.ofSeconds(1)));

        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
        updateOrderRequest.setOrderId(pilotesOrder.getOrderId());
        updateOrderRequest.setNumberOfPilotes(pilotesNumber.get(0));
        updateOrderRequest.setAddressId(address.getAddressId());

        HttpEntity<UpdateOrderRequest> requestHttpEntity = new HttpEntity<>(updateOrderRequest);

        ResponseEntity<ErrorDto> updateResponse = restTemplate.exchange("http://localhost:" + port + "/order/update",
                HttpMethod.PUT,
                requestHttpEntity,
                ErrorDto.class);

        ErrorDto updateResponseBody = updateResponse.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, updateResponseBody.getStatus());
        assertTrue(updateResponseBody.getMessage().contains("More than 5 mins have passed since order has been placed"));

    }

    @Test
    public void updateOrderReturnsErrorIfAddressIsNotFound() {

        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest();
        updateOrderRequest.setOrderId(1);
        updateOrderRequest.setNumberOfPilotes(pilotesNumber.get(0));
        updateOrderRequest.setAddressId(1);

        HttpEntity<UpdateOrderRequest> requestHttpEntity = new HttpEntity<>(updateOrderRequest);

        ResponseEntity<ErrorDto> errorResponse = restTemplate.exchange("http://localhost:" + port + "/order/update",
                HttpMethod.PUT,
                requestHttpEntity,
                ErrorDto.class);

        ErrorDto errorResponseBody = errorResponse.getBody();

        assertTrue(errorResponseBody.getMessage().contains("Cant find address for id 1"));
    }

    private CreateOrderRequest createOrderWithNoClient() {
        CreateOrderRequest createOrderRequestRequest = new CreateOrderRequest();
        createOrderRequestRequest.setNumberOfPilotes(pilotesNumber.get(0));
        createOrderRequestRequest.setAddressId(1);
        createOrderRequestRequest.setClientId(1);
        return createOrderRequestRequest;
    }

    private PilotesOrder storeOrderInDb(Client clientWhoPlaceOrder,
                                        Address address,
                                        LocalDateTime placedOn) {
        return pilotesOrderRepository.save(PilotesOrder.builder()
                .orderTotal(19.95)
                .deliveryAddress(address)
                .client(clientWhoPlaceOrder)
                .pilotes(15)
                .placedOn(placedOn)
                .build());
    }

    private Client storeClientInDb() {
        return clientRepository.save(Client.builder()
                .telephone("123")
                .lastName("lastName")
                .firstName("firstName")
                .build());
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