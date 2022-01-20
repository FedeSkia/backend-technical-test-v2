package com.tui.proof.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PilotesOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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

  @Column(name = "placed_on")
  private LocalDateTime placedOn;

}
