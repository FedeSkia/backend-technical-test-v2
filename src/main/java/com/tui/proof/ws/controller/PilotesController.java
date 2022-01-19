package com.tui.proof.ws.controller;

import com.tui.proof.dto.CreateOrderDto;
import com.tui.proof.exception.ClientDoesntExists;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import com.tui.proof.repository.PilotesOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class PilotesController {

    private final PilotesOrderRepository pilotesOrderRepository;
    private final AddressRepository addressRepository;
    private final ClientRepository clientRepository;

    public PilotesController(PilotesOrderRepository pilotesOrderRepository,
                             AddressRepository addressRepository,
                             ClientRepository clientRepository) {
        this.pilotesOrderRepository = pilotesOrderRepository;
        this.addressRepository = addressRepository;
        this.clientRepository = clientRepository;
    }

    @PostMapping("/order/pilotes")
    public ResponseEntity<PilotesOrder> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {

        Optional<Client> optionalClient = clientRepository.findById(createOrderDto.getClientId());
        if(optionalClient.isEmpty()) {
            throw new ClientDoesntExists("client with id " + createOrderDto.getClientId() + " doesn't exists");
        }

        PilotesOrder savedOrder = null;

        if(createOrderDto.getAddressId() == null){
            List<Address> allByClient = addressRepository.findAllByClient(optionalClient.get());
            savedOrder = pilotesOrderRepository.save(PilotesOrder.builder()
                    .pilotes(createOrderDto.getNumberOfPilotes())

                    .build());
        }

        return ResponseEntity
                .created(URI.create("/client/create"))
                .body(savedOrder);
    }

}
