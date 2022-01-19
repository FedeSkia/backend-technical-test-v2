package com.tui.proof.service;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final AddressRepository addressRepository;

    public ClientService(ClientRepository clientRepository, AddressRepository addressRepository) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
    }

    public CreateClientRequest createNewClient(CreateClientRequest createClientRequest){
        Client client = Client.builder()
                .firstName(createClientRequest.getName())
                .lastName(createClientRequest.getLastName())
                .telephone(createClientRequest.getTelephone())
                .build();

        clientRepository.save(client);

        addressRepository.save(Address.builder()
                .city(createClientRequest.getCity())
                .country(createClientRequest.getCountry())
                .postcode(createClientRequest.getPostcode())
                .street(createClientRequest.getStreet())
                .client(client)
                .build());
        return createClientRequest;
    }

}
