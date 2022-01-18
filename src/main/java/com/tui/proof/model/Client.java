package com.tui.proof.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Client {
  @Id
  @Column(name = "client_id")
  private int clientId;
  private String firstName;
  private String lastName;
  private String telephone;
}
