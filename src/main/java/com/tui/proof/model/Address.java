package com.tui.proof.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
