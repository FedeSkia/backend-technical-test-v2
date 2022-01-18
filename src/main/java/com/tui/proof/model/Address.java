package com.tui.proof.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Address {
  @Id
  @Column(name = "address_id")
  private int addressId;
  private String street;
  private String postcode;
  private String city;
  private String country;

  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;
}
