package com.tui.proof.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PilotesOrder {
  @Id
  @Column(name = "order_id")
  private int orderId;

  @OneToOne
  @JoinColumn(name = "address_id", nullable = false)
  private Address deliveryAddress;

  @OneToOne
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  private int pilotes;
  private double orderTotal;

}
