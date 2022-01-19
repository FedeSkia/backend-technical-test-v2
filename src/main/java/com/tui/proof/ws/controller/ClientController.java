package com.tui.proof.ws.controller;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.service.ClientService;
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

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/client/create")
    public ResponseEntity<CreateClientRequest> createCustomer(@Valid @RequestBody CreateClientRequest createClientRequest) {
        return ResponseEntity
                .created(URI.create("/client/create"))
                .body(clientService.createNewClient(createClientRequest));
    }
}