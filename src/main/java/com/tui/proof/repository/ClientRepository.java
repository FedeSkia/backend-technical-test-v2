package com.tui.proof.repository;

import com.tui.proof.model.Address;
import com.tui.proof.model.Client;
import com.tui.proof.model.PilotesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    Collection<Client> findByFirstNameContainingAndLastNameContainingAndTelephoneContaining(String firstName, String lastName, String telephone);

    @Query("SELECT A FROM Address A WHERE A.client.clientId = ?1")
    List<Address> findAllClientAddresses(Integer clientId);

    @Query("SELECT P FROM PilotesOrder P WHERE P.client.clientId = ?1")
    List<PilotesOrder> findAllClientOrders(Integer clientId);

}
