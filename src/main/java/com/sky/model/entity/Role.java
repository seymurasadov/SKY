package com.sky.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
  @SequenceGenerator(name = "role_seq", sequenceName = "role_id_seq", allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private List<User> users;

}