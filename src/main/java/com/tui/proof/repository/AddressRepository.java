package com.tui.proof.repository;

import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findAllByClient(Client client);

}
