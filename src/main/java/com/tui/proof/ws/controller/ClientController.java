package com.tui.proof.ws.controller;

import com.tui.proof.dto.ClientDto;
import com.tui.proof.model.Client;
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

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostMapping("/client/create")
    public ResponseEntity<ClientDto> createCustomer(@Valid @RequestBody ClientDto clientDto) {
        clientRepository.save(Client.builder()
                .firstName(clientDto.getName())
                .lastName(clientDto.getLastName())
                .telephone(clientDto.getTelephone())
                .build());
        return ResponseEntity
                .created(URI.create("/create"))
                .body(clientDto);
    }
}
