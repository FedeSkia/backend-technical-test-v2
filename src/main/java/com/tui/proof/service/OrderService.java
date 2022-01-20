package com.tui.proof.service;

import com.tui.proof.dto.request.CreateOrderRequest;
import com.tui.proof.dto.request.UpdateOrderRequest;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.exception.AddressNotFound;
import com.tui.proof.exception.ClientDoesntExists;
import com.tui.proof.exception.OrderNotFound;
import com.tui.proof.exception.UpdateTooLate;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

        BigDecimal orderTotal = calculateOrderTotal(createOrderRequest.getNumberOfPilotes());

        PilotesOrder savedOrder = pilotesOrderRepository.save(PilotesOrder.builder()
                .pilotes(createOrderRequest.getNumberOfPilotes())
                .client(optionalClient.get())
                .deliveryAddress(clientAddress)
                .orderTotal(orderTotal.doubleValue())
                .placedOn(LocalDateTime.now())
                .build());

        return PilotesOrderDtoResponse.builder()
                .orderId(savedOrder.getOrderId())
                .client(optionalClient.get())
                .pilotes(createOrderRequest.getNumberOfPilotes())
                .deliveryAddress(clientAddress)
                .orderTotal(orderTotal.doubleValue())
                .build();
    }

    public PilotesOrderDtoResponse updateOrder(UpdateOrderRequest updateOrderRequest){
        Integer orderId = updateOrderRequest.getOrderId();
        Optional<PilotesOrder> orderOptional = pilotesOrderRepository.findById(orderId);
        Address address = addressRepository
                .findById(updateOrderRequest.getAddressId())
                .orElseThrow(() -> new AddressNotFound("Cant find address for id " + updateOrderRequest.getAddressId()));
        PilotesOrder pilotesOrder = orderOptional
                .orElseThrow(() -> new OrderNotFound("Cant find order id " + updateOrderRequest.getOrderId()));

        LocalDateTime placedOn = pilotesOrder.getPlacedOn();
        if(placedOn.isBefore(LocalDateTime.now().minusMinutes(5))){
            throw new UpdateTooLate("More than 5 mins have passed since order has been placed");
        }

        pilotesOrder.setPilotes(updateOrderRequest.getNumberOfPilotes());
        pilotesOrder.setDeliveryAddress(address);
        pilotesOrder.setOrderTotal(calculateOrderTotal(updateOrderRequest.getNumberOfPilotes()).doubleValue());
        PilotesOrder updatedOrder = pilotesOrderRepository.save(pilotesOrder);

        return PilotesOrderDtoResponse.builder()
                .deliveryAddress(updatedOrder.getDeliveryAddress())
                .client(updatedOrder.getClient())
                .orderId(updatedOrder.getOrderId())
                .orderTotal(updatedOrder.getOrderTotal())
                .pilotes(updatedOrder.getPilotes())
                .build();

    }

    private BigDecimal calculateOrderTotal(Integer numberOfPilotes) {
        return BigDecimal.valueOf(numberOfPilotes * 1.33)
                .setScale(2, RoundingMode.HALF_UP);
    }


}
