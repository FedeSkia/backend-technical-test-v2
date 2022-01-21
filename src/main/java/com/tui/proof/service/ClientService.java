package com.tui.proof.service;

import com.tui.proof.dto.request.CreateClientRequest;
import com.tui.proof.dto.request.SearchRequest;
import com.tui.proof.dto.response.AddressResponse;
import com.tui.proof.dto.response.ClientResponse;
import com.tui.proof.dto.response.PilotesOrderDtoResponse;
import com.tui.proof.dto.response.SearchResponse;
import com.tui.proof.mapper.ClientMapper;
import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import com.tui.proof.repository.AddressRepository;
import com.tui.proof.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, AddressRepository addressRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.clientMapper = clientMapper;
    }

    public ClientResponse createNewClient(CreateClientRequest createClientRequest) {

        Client savedClient = clientRepository.save(Client.builder()
                .firstName(createClientRequest.getName())
                .lastName(createClientRequest.getLastName())
                .telephone(createClientRequest.getTelephone())
                .build());

        Address address = addressRepository.save(Address.builder()
                .city(createClientRequest.getCity())
                .country(createClientRequest.getCountry())
                .postcode(createClientRequest.getPostcode())
                .street(createClientRequest.getStreet())
                .client(savedClient)
                .build());

        return clientMapper.toResponse(savedClient, address.getAddressId());
    }

    public List<SearchResponse> search(SearchRequest searchRequest) {

        Collection<Client> clientFound = clientRepository.findByCriteria(searchRequest.getName(), searchRequest.getLastName(), searchRequest.getTelephone());

        return clientFound.stream()
                .map(client -> SearchResponse.builder()
                        .name(client.getFirstName())
                        .lastName(client.getLastName())
                        .telephone(client.getTelephone())
                        .customerAddreses(buildAddresses(client.getClientId()))
                        .orders(buildOrders(clientRepository.findAllClientOrders(client.getClientId())))
                        .build())
                .collect(Collectors.toList());
    }

    private List<AddressResponse> buildAddresses(Integer cliendId) {
        return clientRepository.findAllClientAddresses(cliendId).stream().map(address -> AddressResponse.builder()
                .addressId(address.getAddressId())
                .city(address.getCity())
                .country(address.getCountry())
                .postcode(address.getPostcode())
                .street(address.getStreet())
                .build()).collect(Collectors.toList());
    }

    private List<PilotesOrderDtoResponse> buildOrders(List<PilotesOrder> pilotesOrder) {
        return pilotesOrder.stream()
                .map(order -> {
                    Address deliveryAddress = order.getDeliveryAddress();

                    return PilotesOrderDtoResponse.builder()
                            .orderId(order.getOrderId())
                            .pilotes(order.getPilotes())
                            .orderTotal(order.getOrderTotal())
                            .deliveryAddress(AddressResponse.builder()
                                    .street(deliveryAddress.getStreet())
                                    .addressId(deliveryAddress.getAddressId())
                                    .city(deliveryAddress.getCity())
                                    .postcode(deliveryAddress.getPostcode())
                                    .country(deliveryAddress.getCountry())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }


}
