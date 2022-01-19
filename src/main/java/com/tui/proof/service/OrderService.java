package com.tui.proof.service;

import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.exception.AddressNotFound;
import com.tui.proof.exception.ClientDoesntExists;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class OrderService {

    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;
    private final PilotesOrderRepository pilotesOrderRepository;


    public OrderService(AddressRepository addressRepository,
                        ClientRepository clientRepository,
                        PilotesOrderRepository pilotesOrderRepository) {
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
        this.pilotesOrderRepository = pilotesOrderRepository;
    }

    public PilotesOrderDtoResponse placeOrder(CreateOrderRequest createOrderRequest){
        Optional<Client> optionalClient = clientRepository.findById(createOrderRequest.getClientId());
        if (optionalClient.isEmpty()) {
            throw new ClientDoesntExists("client with id " + createOrderRequest.getClientId() + " doesn't exists");
        }

        Address clientAddress = addressRepository.findById(createOrderRequest.getAddressId())
                .orElseThrow(() -> new AddressNotFound("Cant find address with id " + createOrderRequest.getAddressId()));

        BigDecimal orderTotal = calculateOrderTotal(createOrderRequest);

        PilotesOrder savedOrder = pilotesOrderRepository.save(PilotesOrder.builder()
                .pilotes(createOrderRequest.getNumberOfPilotes())
                .client(optionalClient.get())
                .deliveryAddress(clientAddress)
                .orderTotal(orderTotal.doubleValue())
                .build());

        return PilotesOrderDtoResponse.builder()
                .orderId(savedOrder.getOrderId())
                .client(optionalClient.get())
                .pilotes(createOrderRequest.getNumberOfPilotes())
                .deliveryAddress(clientAddress)
                .orderTotal(orderTotal.doubleValue())
                .build();
    }

    private BigDecimal calculateOrderTotal(CreateOrderRequest createOrderRequest) {
        return BigDecimal.valueOf(createOrderRequest.getNumberOfPilotes() * 1.33)
                .setScale(2, RoundingMode.HALF_UP);
    }


}
