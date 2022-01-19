package com.tui.proof.ws.controller;

import com.tui.proof.dto.ClientDto;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@Log4j2
@RestController
public class ClientController {

    private final ClientRepository clientRepository;

    private final AddressRepository addressRepository;

    public ClientController(ClientRepository clientRepository,
                            AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    @PostMapping("/client/create")
    public ResponseEntity<ClientDto> createCustomer(@Valid @RequestBody ClientDto clientDto) {
        Client client = Client.builder()
                .firstName(clientDto.getName())
                .lastName(clientDto.getLastName())
                .telephone(clientDto.getTelephone())
                .build();

        clientRepository.save(client);

        addressRepository.save(Address.builder()
                .city(clientDto.getCity())
                .country(clientDto.getCountry())
                .postcode(clientDto.getPostcode())
                .street(clientDto.getStreet())
                .client(client)
                .build());

        return ResponseEntity
                .created(URI.create("/client/create"))
                .body(clientDto);
    }
}
